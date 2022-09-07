create table instance_element_metadata
(
    id                                  bigserial not null,
    display_name                        varchar(255),
    key                                 varchar(255),
    integration_metadata_id             int8,
    parent_instance_element_metadata_id int8,
    primary key (id)
);
create table integration_metadata
(
    id                                 bigserial not null,
    integration_display_name           varchar(255),
    source_application_id              varchar(255),
    source_application_integration_id  varchar(255),
    source_application_integration_uri varchar(255),
    version                            int4,
    primary key (id)
);
alter table integration_metadata
    add constraint UniqueSourceApplicationIdAndSourceApplicationIntegrationIdAndVersion unique (source_application_id, source_application_integration_id, version);
alter table instance_element_metadata
    add constraint FK2sghwwdt7ssmph5yp03ra9dpk foreign key (integration_metadata_id) references integration_metadata;
alter table instance_element_metadata
    add constraint FK6n84id9fj5a4e9497fv4ckvcd foreign key (parent_instance_element_metadata_id) references instance_element_metadata;
