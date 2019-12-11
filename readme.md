# A simple Spring Boot app deployed to Azure

Loosely based on this [guide for a Boot app](https://docs.microsoft.com/en-us/azure/spring-cloud/spring-cloud-quickstart-launch-app-portal)
and this [guide for Spring Cloud](https://docs.microsoft.com/en-us/azure/spring-cloud/spring-cloud-tutorial-prepare-app-deployment).

## Configure Azure Spring Cloud

1.  Login to Azure with `az login`.

1.  Add the Azure Spring Cloud (ASC) extension.
    
    ```bash
    az extension add --name spring-cloud
    ```

1.  Create an [Azure Spring Cloud instance](https://portal.azure.com/#create/Microsoft.AppPlatform)
    in the Azure portal.

1.  [Configure the Config Server](https://docs.microsoft.com/en-us/azure/spring-cloud/spring-cloud-quickstart-launch-app-portal#set-up-your-configuration-server)
    in the Azure portal.
    
1.  Create _application.properties_ file in your configuration
    repository that looks like [this one](https://github.com/tygern/pal-tracker-azure/blob/config/application.properties)

1.  Configure the default group and Spring Cloud instance

    ```bash
    az configure --defaults group=${RESOURCE_GROUP_NAME}
    az configure --defaults spring-cloud=${SERVICE_INSTANCE_NAME}
    ```

## Create the timesheets app on Azure

1.  Create an app on ASC.

    ```bash
    az spring-cloud app create -n timesheets
    ```

1.  Create a Azure MySQL database instance.
    
    ```bash
    az mysql server create --resource-group ${RESOURCE_GROUP_NAME} --name timesheets-db  --location westus --admin-user pal --admin-password ${DB_USERNAME} --sku-name B_Gen5_1 --version 5.7
    ```

    Due to a [known issue](https://github.com/flyway/flyway/issues/2519)
    stick to MySQL v5.7 rather than 8 until [flyway v6.1.2](https://github.com/flyway/flyway/milestone/49)
    is released.

1.  Configure the database by clicking _Connection Security_ from the
    database's view page.
    1.  Add your IP address to allow access.
    1.  Allow access to Azure services.
    1.  Disable SSL connection enforcement.

1.  Create the _timesheets_ database on your Azure MySQL database
    instance.
    
    ```bash
    mysql --host timesheets-db.mysql.database.azure.com --user pal@timesheets-db --password=${DB_PASSWORD} -e "create database timesheets;"
    ```

1.  Create a service binding between the timesheets app and the new
    database instance using items 6-8 on [this guide](https://docs.microsoft.com/en-us/azure/spring-cloud/spring-cloud-tutorial-bind-mysql#bind-your-app-to-your-azure-database-for-mysql-instance).
    Use _pal@timesheets-db_ as the username and the password you
    provided above as the password.

## Build the timesheets app locally

1.  Create local databases.
    ```bash
    mysql -uroot < databases/create_databases.sql
    ```

1.  Run migrations.
    ```bash
    ./gradlew flywayMigrate testMigrate
    ```

1.  Build the timesheets app.
    ```bash
    ./gradlew clean build
    ```

## Deploy the timesheets app to Azure

1.  Assign a [public endpoint](https://docs.microsoft.com/en-us/azure/spring-cloud/spring-cloud-quickstart-launch-app-portal#assign-a-public-endpoint-to-gateway)
    to the timesheets application.

1.  Migrate the production database.

    ```bash
    ./gradlew prodMigrate -PjdbcUser="pal@timesheets-db" -PjdbcPassword=${DB_PASSWORD} -PjdbcUrl='jdbc:mysql://timesheets-db.mysql.database.azure.com:3306/timesheets?useTimezone=true&serverTimezone=UTC&useLegacyDatetimeCode=false'
    ```

1.  Deploy the jar file.
    ```bash
    az spring-cloud app deploy -n timesheets --jar-path ./build/libs/pal-tracker.jar
    ```

1.  Exercise the [endpoints from IntelliJ](requests.http).

1.  [View the logs](https://docs.microsoft.com/en-us/azure/spring-cloud/diagnostic-services) of the timesheets app.
