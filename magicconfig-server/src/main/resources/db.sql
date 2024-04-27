CREATE TABLE IF NOT EXISTS `configs`
(
    `app`  VARCHAR(64)  NOT NULL,
    `env`  VARCHAR(64)  NOT NULL,
    `ns`   VARCHAR(64)  NOT NULL,
    `pkey` VARCHAR(64)  NOT NULL,
    `pval` VARCHAR(128) NULL
);

INSERT INTO `configs`(`app`, `env`, `ns`, `pkey`, `pval`)
VALUES ('app1', 'dev', 'public', 'magic.a', 'dev100'),
       ('app1', 'dev', 'public', 'magic.b', 'http://localhost:9129'),
       ('app1', 'dev', 'public', 'magic.c', 'cc100');
