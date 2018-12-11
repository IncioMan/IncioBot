FROM php:7.0-apache
MAINTAINER incio
COPY site /var/www/html/
COPY euei_site_startup.sh /
ENTRYPOINT ["/bin/bash", "/euei_site_startup.sh"]
CMD ["apache2-foreground"]