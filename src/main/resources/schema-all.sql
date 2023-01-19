--DROP TABLE resident IF EXISTS;
CREATE TABLE resident
(
aptcd VARCHAR(5) not null,
orgaptcd VARCHAR(5) not null,
dongho VARCHAR(10) not null,
rel VARCHAR(10) ,
name VARCHAR(10) ,
mobile_tel_no1 VARCHAR(13) ,
office_tel_no1 VARCHAR(13),
office_tel_no2 VARCHAR(13),
sms_tel_no1 VARCHAR(13) ,
sms_tel_no2 VARCHAR(13) ,
delflag VARCHAR(2) ,
occu_date VARCHAR(8) default '00000000',
reg_date VARCHAR(8) default '00000000',
upd_date VARCHAR(8) default '00000000',
modifydt VARCHAR(8) default '00000000'

);

--DROP TABLE apt IF EXISTS;
CREATE TABLE apt
(
aptcd VARCHAR(5) not null,
orgaptcd VARCHAR(5) not null,
aptnm	 VARCHAR(100)
);