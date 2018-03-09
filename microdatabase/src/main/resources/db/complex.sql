
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
	S.id as sessionid,
	S.leadercf,
	S.patientcf,
    L.logtime as Date,
    HP.acronym,
    HP.name as HealthParameter,
    L.healthparametervalue as ActualValue
from Session as S
inner join Log as L on L.sessionid = S.id
inner join HealthParameter as HP on L.healthparameterid = HP.id
where S.id = '$id'
order by 4;

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
    generate task_report_json
**/
select array_to_json(array_agg(row_to_json(t))) from (
select
		S.id as sessionId,
		T.name as taskStrId,
		S.leadercf as leaderCF,
		S.patientcf as patientCF,
		A.acronym as activityAcronym,
		A.name as activityName,
		A.hps as relatedHealthParameters,
		T.starttime as startTime,
		T.endtime as endTime,
		O.operatorcf as operatorCF
	from Session as S
	left outer join Task as T on T.sessionid = S.id
	left outer join (
		select A.id, A.acronym, A.name, array_agg(distinct HP.name) as hps
		from Activity as A
		inner join HealthParameter as HP on A.healthparameterids @> Array[HP.id]
		group by 1, 2, 3
	) as A on T.activityid = A.id
	left outer join Operator as O on T.operatorcf = O.operatorcf
	where A.name is not NULL and S.id = '$id'
	order by T.starttime
) t;