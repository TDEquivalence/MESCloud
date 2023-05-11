# Development Environment

## Components:

- REST API
- MQTT Client
- PostgreSQL Database

## Setting the Environment

The development environment can be configured by using an **application properties file** or/and by setting 
**environment variables** on your OS. Both options can be used exclusively or at the same time.

### Application properties file

- On the project root (MESCloud), copy the **application.sample.yaml** file, renaming it **application.yaml**
- Uncomment all the desired configuration values and replace their placeholders (search for **your_MES** or **your_TDE**)
by their actual value
- **NOTE** that any set environment variables will take precedence over placeholder values. To avoid this
behavior, simply remove the environment variable from any given property (ex.: change
**${MES_DB_PASSWORD:your_MES_db_password}** to  **your_MES_db_password**)

### Environment Variables

- From the project root (MESCloud), change directory to  **automation** and copy the **set-sys-env-vars.example.bat** 
file, renaming it **set-sys-env-vars.bat**
- Replace the desired placeholders (search for **your_MES** or **your_TDE**) by their actual value
- Run the script on the command line (press Windows+R and insert cmd on the prompt window) 
- Optionally, you can remove sensitive data from the script, such as your TDE AWS userEntity secret access keys, after setting
the required environment variables

```
   cd automation
   .\set-sys-env-vars.bat
```

### Getting the Environment Variables

Use the following table to look for the correct value for each variable:

| Placeholder               | Value location                                                         | Value example                                   |
|---------------------------|------------------------------------------------------------------------|-------------------------------------------------|
| MES_DB_HOST               | Your local DB config                                                   | localhost                                       |
| MES_DB_PORT               | Your local DB config                                                   | 5432                                            |
| MES_DB_NAME               | Your local DB config                                                   | mescloud                                        |
| MES_DB_USERNAME           | Your local DB config                                                   | myusername                                      |
| MES_DB_PASSWORD           | Your local DB config                                                   | mypassword                                      |
|                           |                                                                        |                                                 |
| MES_MQTT_CLIENT_ID        | AWS IoT Core -> All devices -> Things                                  | DEV_BE_UI                                       |
| MES_MQTT_CLIENT_ENDPOINT  | AWS IoT Core -> Connect -> Connect one device (point 4, remove "ping") | apt3us2jmma1-tts.iot.eu-central-1.amazonaws.com |
| TDE_AWS_ACCESS_KEY_ID     | AWS IAM -> My security credentials -> Access keys                      | -                                               |
| TDE_AWS_SECRET_ACCESS_KEY | AWS IAM -> My security credentials -> Access keys                      | -                                               |
