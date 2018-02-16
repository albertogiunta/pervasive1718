
-- Temp ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(4, null, 36.0, 37.0, 0.0, 999.0, 0.5, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(4, null, 0.0, 35.0, 0.0, 999.0, 0.5, 'Ipotermia', false);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(4, null, 38.0, 38.5, 0.0, 999.0, 0.5, 'Febbre', false);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(4, null, 39.0, 39.5, 0.0, 999.0, 0.5, 'Febbre Elevata', false);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(4, null, 40.0, 666.66, 0.0, 999.0, 0.5, 'Iperpiressia', false);

-- SatO2+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(5, null, 90.0, 94.0, 0.0, 999.0, 0.5, 'Ipossia', false);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(5, null, 95.0, 100.0, 0.0, 999.0, 0.5, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(5, null, 0.0, 9.0, 0.0, 999.0, 0.5, 'Ipossia Grave', false);

-- HR +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 100.0, 160.0, 0.0, 0.1, 0.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 90.0, 150.0, 0.1, 0.5, 0.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 80.0, 140.0, 0.5, 1.0, 0.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 80.0, 130.0, 1.0, 3.0, 0.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 80.0, 120.0, 3.0, 6.0, 0.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 70.0, 110.0, 6.0, 11.0, 0.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 60.0, 105.0, 11.0, 15.0, 0.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 60.0, 100.0, 15.0, 20.0, 0.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 50.0, 80.0, 20.0, 999.9, 0.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 100.0, 150.0, 20.0, 999.9, 0.0, 'Tachicardia', false);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(3, null, 45.0, 60.0, 20.0, 999.0, 0.0, 'Bradicardia', false);

-- Pressione Arteriosa SYS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(1, null, 75.0, 100.0, 0.0, 1.0, 5.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(1, null, 80.0, 110.0, 1.0, 6.0, 5.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(1, null, 85.0, 120.0, 6.0, 13.0, 5.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(1, null, 95.0, 140.0, 13.0, 999.9, 5.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(1, null, 140.0, 159.0, 13.0, 999.9, 1.0, 'Ipertensione 1', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(1, null, 160.0, 179.0, 13.0, 999.9, 1.0, 'Ipertensione 2', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(1, null, 180.0, 666.66, 13.0, 999.9, 1.0, 'Ipertensione 3', true);

-- Pressione Arteriosa DIA ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(2, null, 50.0, 70.0, 0.0, 1.0, 5.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(2, null, 50.0, 80.0, 1.0, 4.0, 5.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(2, null, 55.0, 80.0, 6.0, 13.0, 5.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(2, null, 60.0, 90.0, 13.0, 999.9, 5.0, 'Normale', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(2, null, 90.0, 99.0, 13.0, 999.9, 1.0, 'Ipertensione 1', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(2, null, 100.0, 109.0, 13.0, 999.9, 1.0, 'Ipertensione 2', true);

insert into boundary (healthparameterid, activityid, lowerbound, upperbound, minage, maxage, lightwarning_offset, status, itsgood)
values(2, null, 110.0, 666.6, 13.0, 999.9, 5.0, 'Ipertensione 3', true);
