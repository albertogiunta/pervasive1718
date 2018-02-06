
/**
Selects all the operators with their respective roles
**/
select 
	O.id, 
    O.name, 
    O.surname, 
    R.name, 
    O.isActive as Online 
from Operator as O 
inner join Role as R on O.roleid = R.id;

/**
Select all the logs with their respective health parameter
**/
select 
	L.id, 
    L.logtime, 
    L.name as Descr, 
    HP.acronym, 
    HP.name as HealthParameter, 
    L.Value as ActualValue
from Log as L 
inner join HealthParameter as HP on L.healthparameterid = HP.id;

/**
Select all the health parameters with their respective actual values and *static* boundaries

It *should* let you see only the last values by ordering in descending order and grouping by life parameter
**/
select 
	HP.id, 
    L.logtime,
    HP.acronym, 
    HP.name as HealthParameter, 
    L.Value as ActualValue,
    B.upperbound as Max,
    B.lowerbound as Min
from HealthParameter as HP 
left outer join Log as L on L.healthparameterid = HP.id
left outer join Boundary as B on B.healthparameterid = HP.id
order by L.logtime desc
group by HealthParameter;

/**
Select all the possible activities with their respective types
**/
select 
	A.id, 
    A.acronym,
    A.name as Activity, 
    AT.name as Type
from Activity as A 
inner join ActivityType as AT on A.activitytypeid = AT.id;

/**
Select all the Activities with relative life parameters and relative boundaries

Grouped by Activity
**/
select 
	A.id, 
    A.acronym,
    A.name as Activity, 
    AT.name as Type,
   	HP.name as HealthParameter,
   	B.lowerbound as Min,
    B.upperbound as Max
from Activity as A 
left outer join ActivityType as AT on A.activitytypeid = AT.id
left outer join Boundary as B on A.boundaryid = B.id
left outer join HealthParameter as HP on B.healthparameterid = HP.id
order by Activity;

/**
Select all the Activities with relative life parameters and relative boundaries

Grouped by Life Parameters
**/
select 
	A.id, 
    A.acronym,
    A.name as Activity, 
    AT.name as Type,
   	HP.name as HealthParameter,
   	B.lowerbound as Min,
    B.upperbound as Max
from Activity as A 
left outer join ActivityType as AT on A.activitytypeid = AT.id
left outer join Boundary as B on A.boundaryid = B.id
left outer join HealthParameter as HP on B.healthparameterid = HP.id
order by HealthParameter;

/**
Selects all the tasks with all the snuff and stuff
**/
select 
	T.id,
    A.acronym,
    A.name as Activity, 
    AT.name as Type,
	HP.name as HealthParameter,
   	TS.name as Status,
    T.starttime as Started,
    T.endtime as Ended
from Task as T
left outer join TaskStatus as TS on T.statusid = TS.id
left outer join Activity as A on T.activityid = A.id
left outer join ActivityType as AT on A.activitytypeid = AT.id
left outer join Boundary as B on A.boundaryid = B.id
left outer join HealthParameter as HP on B.healthparameterid = HP.id
left outer join Operator as O on T.operatorid = O.id
left outer join Role as R on O.roleid = R.id
order by Activity;

/**
Selects all the tasks with all the snuff and stuff
**/
select 
	T.id,
    A.acronym,
    A.name as Activity, 
    AT.name as Type,
	HP.name as HealthParameter,
   	TS.name as Status,
    T.starttime as Started,
    T.endtime as Ended
from Task as T
left outer join TaskStatus as TS on T.statusid = TS.id
left outer join Activity as A on T.activityid = A.id
left outer join ActivityType as AT on A.activitytypeid = AT.id
left outer join Boundary as B on A.boundaryid = B.id
left outer join HealthParameter as HP on B.healthparameterid = HP.id
left outer join Operator as O on T.operatorid = O.id
left outer join Role as R on O.roleid = R.id
order by Started;