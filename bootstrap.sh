#!/usr/bin/env bash

# Use single quotes instead of double quotes to make it work with special-character passwords
DBROOTPASSWORD='12345678'
DBPASSWORD='12345678'
DBUSERNAME='siteindex'
DATABASE='siteindex'

# update / upgrade
echo updating os
sudo apt-get update > /dev/null

# install apache 2.5 and php 5.5
echo installing apache
sudo apt-get install -y apache2 > /dev/null
echo installing php5
sudo apt-get install -y php5 > /dev/null

# install mysql and give password to installer
sudo debconf-set-selections <<< "mysql-server mysql-server/root_password password $DBROOTPASSWORD"
sudo debconf-set-selections <<< "mysql-server mysql-server/root_password_again password $DBROOTPASSWORD"
echo installing mysql-server
sudo apt-get -y install mysql-server > /dev/null
echo installing php5-mysql
sudo apt-get install php5-mysql > /dev/null

# install phpmyadmin and give password(s) to installer
# for simplicity I'm using the same password for mysql and phpmyadmin
sudo debconf-set-selections <<< "phpmyadmin phpmyadmin/dbconfig-install boolean true"
sudo debconf-set-selections <<< "phpmyadmin phpmyadmin/app-password-confirm password $DBROOTPASSWORD"
sudo debconf-set-selections <<< "phpmyadmin phpmyadmin/mysql/admin-pass password $DBROOTPASSWORD"
sudo debconf-set-selections <<< "phpmyadmin phpmyadmin/mysql/app-pass password $DBROOTPASSWORD"
sudo debconf-set-selections <<< "phpmyadmin phpmyadmin/reconfigure-webserver multiselect apache2"
echo installing phpmyadmin
sudo apt-get -y install phpmyadmin > /dev/null

# Setup database
echo creating database $DATABASE
mysql -uroot -p$DBROOTPASSWORD -e "CREATE DATABASE $DATABASE;"

# Make MySQL external accessible
echo `mysql -uroot -p$DBROOTPASSWORD -e "GRANT ALL PRIVILEGES ON *.* TO '$DBUSERNAME'@'%' IDENTIFIED BY '$DBPASSWORD';"`
echo `mysql -uroot -p$DBROOTPASSWORD -e "GRANT ALL PRIVILEGES ON *.* TO '$DBUSERNAME'@'localhost' IDENTIFIED BY '$DBPASSWORD';"`
echo `mysql -uroot -p$DBROOTPASSWORD -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '$DBROOTPASSWORD';"`
echo `mysql -uroot -p$DBROOTPASSWORD -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY '$DBROOTPASSWORD';"`
sed -i 's/^bind-address/#bind-address/' /etc/mysql/my.cnf
sed -i 's/^skip-external-locking/#skip-external-locking/' /etc/mysql/my.cnf
sudo service mysql restart

# Import bootstrap SQL
echo importing database objects
mysql -uroot -p$DBROOTPASSWORD $DATABASE < /vagrant/sql/create.sql

# enable mod_rewrite
sudo a2enmod rewrite

# restart apache
sudo service apache2 restart

# activate mcrypt
cd /etc/php5/mods-available
sudo php5enmod mcrypt

# restart apache
sudo service apache2 restart