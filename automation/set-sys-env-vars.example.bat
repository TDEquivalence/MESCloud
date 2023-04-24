:: Search for your_MES or your_TDE to find placeholders

:: Spring
:: setx SPRING_PROFILES_ACTIVE=production

:: Database
:: setx MES_DB_HOST your_MES_db_host
:: setx MES_DB_PORT your_MES_db_port
:: setx MES_DB_NAME your_MES_db_name
:: setx MES_DB_USERNAME your_MES_db_username
:: setx MES_DB_PASSWORD your_MES_db_password

:: AWS IoT
:: setx MES_MQTT_CLIENT_ID your_MES_BE_thing
:: setx MES_MQTT_CLIENT_ENDPOINT your_MES_BE_thing
:: setx TDE_AWS_ACCESS_KEY_ID your_TDE_user_aws_access_key_id
:: setx TDE_AWS_SECRET_ACCESS_KEY your_TDE_user_aws_secret_access_key

echo "Spring configuration: %SPRING_PROFILES_ACTIVE%"
echo "Database configuration: %MES_DB_HOST% %MES_DB_PORT% %MES_DB_NAME% %MES_DB_USERNAME% %MES_DB_PASSWORD%"
echo "AWS IoT configuration: %MES_MQTT_CLIENT_ID% %MES_MQTT_CLIENT_ENDPOINT% %TDE_AWS_ACCESS_KEY_ID% %TDE_AWS_SECRET_ACCESS_KEY%"
