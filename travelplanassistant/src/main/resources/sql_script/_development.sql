SELECT * FROM comp8047.users;

SELECT * FROM comp8047.travel_plans;

SELECT * FROM comp8047.user_plan_status;

SELECT * from travel_plans; 
-- public 3 4 7 8
-- private 5


SELECT *  from user_plan_status; -- where travel_plan_id = 7;-- and user_id = 8; 
-- owner 1,5,4
-- 

SELECT * FROM comp8047.travel_plans; -- where owner_id = 2;
SELECT * FROM comp8047.users where id = 4;
-- update travel_plans set status = 'NEW', cancelled_at = null, cancellation_reason = null where id = 8;

-- 1. NEW travel plan--------------------------------------------------------------------------------------------------------------
-- owner 10: test10@gmail.com
SELECT * FROM comp8047.users where first_name like '%Alice%';

-- 60 - 104   no plans
select * from users u left join user_plan_status ups on u.id = ups.user_id where ups.id is null;

-- 9
select tp.* from travel_plans tp where tp.owner_id = 10;
select * from user_plan_status ups where travel_plan_id = 9;

-- 2. IN_PROGRESS travel plan--------------------------------------------------------------------------------------------------------------
-- plan_id 7,8
-- owner 2: weinaxu1986@hotmail.com
-- owner 3: test1@gmail.com
SELECT tp.* FROM comp8047.user_plan_status ups left join travel_plans tp 
on ups.travel_plan_id = tp.id
where tp.status = 'IN_PROGRESS';

-- owner
select * from users where id in (2,3);

-- plan members
-- plan_id: 7; members: 3,6: test4@gmail.com
-- plan_id: 8; members: 2,7,8: test5@gmail.com; test6@gmail.com
select * from user_plan_status where travel_plan_id in (7,8);
select * from users where id in (6, 7, 8);

