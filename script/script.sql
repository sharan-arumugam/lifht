ALTER TABLE `lifht`.`employee` 
CHANGE COLUMN `active` `active` CHAR(1) NOT NULL DEFAULT 'N' ;

INSERT INTO `lifht`.`role_master` (`role_id`, `role`) VALUES ('100', 'ROLE_EMPLOYEE');
INSERT INTO `lifht`.`role_master` (`role_id`, `role`) VALUES ('200', 'ROLE_ADMIN');
INSERT INTO `lifht`.`role_master` (`role_id`, `role`) VALUES ('300', 'ROLE_SUPER');

INSERT INTO `lifht`.`employee` (`ps_number`, `active`, `password`) VALUES ('employee', 'Y', '$2a$10$PWzFaRS61ZIx4jx5BEY.MOLaWVOuQKKd50YGxainAEvFPgkPxCDWq');
INSERT INTO `lifht`.`employee` (`ps_number`, `active`, `password`) VALUES ('admin', 'A', '$2a$10$q5hM4C6EVl2KNbEgcbA8t.4MzNhiDOO.7EbR.6P247j0jY3MqsI1m');
INSERT INTO `lifht`.`employee` (`ps_number`, `active`, `password`) VALUES ('super', 'A', '$2a$10$SlQen.lmrDe6SLBMQc84g.bKyWmO6aRq5f/gVQFx0YtWHAQF8k73q');
INSERT INTO `lifht`.`employee` (`ps_number`, `active`, `password`) VALUES ('inactive', 'N', '$2a$10$PWzFaRS61ZIx4jx5BEY.MOLaWVOuQKKd50YGxainAEvFPgkPxCDWq');

INSERT INTO `lifht`.`employee_roles` (`employee_ps_number`, `roles_role_id`) VALUES ('employee', '100');
INSERT INTO `lifht`.`employee_roles` (`employee_ps_number`, `roles_role_id`) VALUES ('admin', '200');
INSERT INTO `lifht`.`employee_roles` (`employee_ps_number`, `roles_role_id`) VALUES ('super', '300');
