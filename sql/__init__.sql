-- auto-generated definition
create table user
(
    UserId   bigint unsigned auto_increment comment '帳戶ID'
        primary key,
    Username varchar(30) not null comment '登录账户',
    Password varchar(60) null comment '登录密码',
    Status   tinyint     not null comment '状态,1-启用,0-禁用'
)
    comment '用户（賬戶）表' engine = InnoDB;

