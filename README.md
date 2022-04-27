# spring-security-email-validation

to learn spring security email validation

## Demo

![demo](./figures/demo.gif)

## 구상도

![blueprint](./figures/blueprint.png)

## PostgreSQL Container Run

```bash
docker pull postgres

docker run \
    --name registrationDB \
    -p 5433:5432 \
    -e POSTGRES_USER=admin \
    -e POSTGRES_PASSWORD=pwd \
    -e POSTGRES_DB=registration \
    -d \
    postgres
```

### Inspect Database

```bash
docker exec -it <PSQL-Container-ID> bash
#           호스트     |  포트 |  유저  |  DB     
$ psql -h localhost -p 5432 -U admin registration -W # to authenticate to start using as postgres user
```

![inspect db](./figures/inspect-db.png)

## MailDev RUN

```bash
docker run -p 1080:1080 -p 1025:1025 maildev/maildev
```

![mail dev](./figures/maildev.png)

