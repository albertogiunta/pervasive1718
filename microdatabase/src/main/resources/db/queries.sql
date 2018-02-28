/*
select A.Signature, A.name as ActivityName, A.ExpectedEffect, A.StatusID, AT.Name as ActivityType
from activity as A inner join activitytype as AT on A.typeid = AT.id
order by 1;

insert into Role(name) values ('Leader');
insert into Role(name) values ('Collaboratore');
insert into Role(name) values ('Anestesista');

insert into activity (name, expectedeffect, typeid, signature) values (Intubazione Orotracheale', 'To Be Added', 3,  'IOT');
insert into activity (name, expectedeffect, typeid, signature) values (Presidio Sovraglottico', 'To Be Added', 3,  'SGA');
insert into activity (name, expectedeffect, typeid, signature) values (Fibroscopia', 'To Be Added', 3,  'FibrScop');
insert into activity (name, expectedeffect, typeid, signature) values (Crico-Tiro', 'To Be Added', 3,  'CT');
insert into activity (name, expectedeffect, typeid, signature) values (Drenaggio Toracico', 'To Be Added', 3,  'DT');
insert into activity (name, expectedeffect, typeid, signature) values (Decompressione Pleurica', 'To Be Added', 3,  'DCP');
insert into activity (name, expectedeffect, typeid, signature) values (Infusore a Pressione', 'To Be Added', 3,  'IaP');
insert into activity (name, expectedeffect, typeid, signature) values (Intraossea', 'To Be Added', 3,  'InOs');
insert into activity (name, expectedeffect, typeid, signature) values (Catetere Arterioso', 'To Be Added', 3,  'CatArt');
insert into activity (name, expectedeffect, typeid, signature) values (Emostasi', 'To Be Added', 3,  'Emo');
insert into activity (name, expectedeffect, typeid, signature) values (Pelvic Binder', 'To Be Added', 3,  'PelBin');
insert into activity (name, expectedeffect, typeid, signature) values (Fissatore Esterno', 'To Be Added', 3,  'ExFix');
insert into activity (name, expectedeffect, typeid, signature) values (Tourniquet', 'To Be Added', 3,  'Tour');
insert into activity (name, expectedeffect, typeid, signature) values (Reboa', 'To Be Added', 3,  'Reboa');
insert into activity (name, expectedeffect, typeid, signature) values (Toracotomia Resuscitativa', 'To Be Added', 3,  'TorRez');
insert into activity (name, expectedeffect, typeid, signature) values (Laparotomia Urgente', 'To Be Added', 3,  'LapUrg');
insert into activity (name, expectedeffect, typeid, signature) values (Sondino Nasogastrico', 'To Be Added', 3,  'SNG');
insert into activity (name, expectedeffect, typeid, signature) values (Sondino Orogastrico', 'To Be Added', 3,  'SOG');
insert into activity (name, expectedeffect, typeid, signature) values (Cistostomia', 'To Be Added', 3,  'Cist');
insert into activity (name, expectedeffect, typeid, signature) values (Foley Vescicale', 'To Be Added', 3,  'FoleyVesc');
insert into activity (name, expectedeffect, typeid, signature) values (Escarotomia', 'To Be Added', 3,  'Esc');
insert into activity (name, expectedeffect, typeid, signature) values (CVC', 'To Be Added', 3,  'CVC');
insert into activity (name, expectedeffect, typeid, signature) values (Allineamento Fratture', 'To Be Added', 3,  'FractAl');
insert into activity (name, expectedeffect, typeid, signature) values (ALR', 'To Be Added', 3,  'ALR');
insert into activity (name, expectedeffect, typeid, signature) values (ALS', 'To Be Added', 3,  'ALS');

insert into HealthParameter (name, signature) values ('Pressione Arteriosa', 'SYS');
insert into HealthParameter (name, signature) values ('Pressione Arteriosa', 'DIA');
insert into HealthParameter (name, signature) values ('Frequenza Cardiaca', 'HR');
insert into HealthParameter (name, signature) values ('Temperatura', 'T');
insert into HealthParameter (name, signature) values ('Saturazione Ossigeno', 'SpO2');
insert into HealthParameter (name, signature) values ('End Tidal Anidride Carbonica', 'EtCO2');

insert into activity (name, expectedeffect, typeid, signature) values ('Cristalloidi', 'To Be Added', 1,  'Cristal');
insert into activity (name, expectedeffect, typeid, signature) values ('Soluzione Ipertonica', 'To Be Added', 1,  'SolIper');
insert into activity (name, expectedeffect, typeid, signature) values ('P.T.M.', 'To Be Added', 1,  'PTM');
insert into activity (name, expectedeffect, typeid, signature) values ('Crioprecipitati', 'To Be Added', 1,  'Crio');
insert into activity (name, expectedeffect, typeid, signature) values ('Zero Negativo', 'To Be Added', 1,  'ZeroNeg');
insert into activity (name, expectedeffect, typeid, signature) values ('Emazie Concentrate,', 'To Be Added', 1,  'EmConc');
insert into activity (name, expectedeffect, typeid, signature) values ('Fibrinogeno,', 'To Be Added', 1,  'Fibr');
insert into activity (name, expectedeffect, typeid, signature) values ('Plasma Fresco Congelato', 'To Be Added', 1,  'PFC');
insert into activity (name, expectedeffect, typeid, signature) values ('Piastrine', 'To Be Added', 1,  'Pstn');
insert into activity (name, expectedeffect, typeid, signature) values ('Complesso Protrombinico', 'To Be Added', 1,  'CompProt');
insert into activity (name, expectedeffect, typeid, signature) values ('Acido Tranexamico', 'To Be Added', 1,  'AcTra');
insert into activity (name, expectedeffect, typeid, signature) values ('Morfina', 'To Be Added', 1,  'Morf');
insert into activity (name, expectedeffect, typeid, signature) values ('Mannitolo', 'To Be Added', 1,  'Man');
insert into activity (name, expectedeffect, typeid, signature) values ('Propofol', 'To Be Added', 1,  'Prop');
insert into activity (name, expectedeffect, typeid, signature) values ('Ketamina', 'To Be Added', 1,  'Keta');
insert into activity (name, expectedeffect, typeid, signature) values ('Succinilcolina', 'To Be Added', 1,  'Succ');
insert into activity (name, expectedeffect, typeid, signature) values ('Midazolam', 'To Be Added', 1,  'Mida');
insert into activity (name, expectedeffect, typeid, signature) values ('Fentanil', 'To Be Added', 1,  'Fenta');
insert into activity (name, expectedeffect, typeid, signature) values ('Curaro', 'To Be Added', 1,  'Cur');
insert into activity (name, expectedeffect, typeid, signature) values ('Tiopentone', 'To Be Added', 1,  'Tio');
insert into activity (name, expectedeffect, typeid, signature) values ('Adrenalina', 'To Be Added', 1,  'Adren');
insert into activity (name, expectedeffect, typeid, signature) values ('Noradrenalina', 'To Be Added', 1,  'Noradren');
insert into activity (name, expectedeffect, typeid, signature) values ('Dopamina', 'To Be Added', 1,  'Dop');
insert into activity (name, expectedeffect, typeid, signature) values ('Fentanil (50mcg/ml)', 'To Be Added', 1,  'Fenta50');
insert into activity (name, expectedeffect, typeid, signature) values ('Propofol (20mg/ml)', 'To Be Added', 1,  'Prop20');
insert into activity (name, expectedeffect, typeid, signature) values ('Ketanest (25mcg + 25mg/ml)', 'To Be Added', 1,  'Keta25');
insert into activity (name, expectedeffect, typeid, signature) values ('Midazolam  (5mg/ml)', 'To Be Added', 1,  'Mida5');
insert into activity (name, expectedeffect, typeid, signature) values ('Tiopentone (2g/50ml)', 'To Be Added', 1,  'Tio2');
insert into activity (name, expectedeffect, typeid, signature) values ('Farmaco Generico', 'To Be Added', 1,  'Gen');
insert into activity (name, expectedeffect, typeid, signature) values ('Atropina', 'To Be Added', 1,  'Atro');
insert into activity (name, expectedeffect, typeid, signature) values ('Shock Elettrico', 'To Be Added', 1,  'eShock');
insert into activity (name, expectedeffect, typeid, signature) values ('Profilassi ABT', 'To Be Added', 1,  'Prof.ABT');

insert into activity (name, expectedeffect, typeid, signature) values ('Ecografia (Echofast)', 'To Be Added', 2,  'Echo');
insert into activity (name, expectedeffect, typeid, signature) values ('Angiografia', 'To Be Added', 2,  'Angio');
insert into activity (name, expectedeffect, typeid, signature) values ('RX Torace', 'To Be Added', 2,  'RXTor');
insert into activity (name, expectedeffect, typeid, signature) values ('TC Cerebrale-Cervicale', 'To Be Added', 2,  'TCCrCv');
insert into activity (name, expectedeffect, typeid, signature) values ('RX Bacino', 'To Be Added', 2,  'RXBac');
insert into activity (name, expectedeffect, typeid, signature) values ('TC Toraco-Addominale', 'To Be Added', 2,  'MdC');
insert into activity (name, expectedeffect, typeid, signature) values ('RX Scheletro', 'To Be Added', 2,  'RXSkel');
insert into activity (name, expectedeffect, typeid, signature) values ('Angio TC', 'To Be Added', 2,  'AngioTC');
insert into activity (name, expectedeffect, typeid, signature) values ('RMN', 'To Be Added', 2,  'RMN');
insert into activity (name, expectedeffect, typeid, signature) values ('WB-TC', 'To Be Added', 2,  'WB-TC');
insert into activity (name, expectedeffect, typeid, signature) values ('EGA', 'To Be Added', 2,  'EGA');
insert into activity (name, expectedeffect, typeid, signature) values ('ROTEM', 'To Be Added', 2,  'ROTEM');
insert into activity (name, expectedeffect, typeid, signature) values ('Ematochimica', 'To Be Added', 2,  'Ematoc');
insert into activity (name, expectedeffect, typeid, signature) values ('Tossicologico', 'To Be Added', 2,  'Tox');
*/

--delete from activity where id = 11;
--insert into activity (name, expectedeffect, typeid, signature) values ('Do Be Added', 'Do Be Added', 3,  'ALS');
--update activity set name = 'To Be Added' where name = 'Do Be Added';

--update activity set expectedEffect = 'To Be Added' where expectedEffect = 'Do Be Added';

select A.Signature, A.name as ActivityName, A.ExpectedEffect, AT.Name as ActivityType
from activity as A inner join activitytype as AT on A.typeid = AT.id
where AT.id = 3
order by 1, A.id;

ALTER TABLE activity DROP COLUMN boundaryid
ALTER TABLE activity ADD COLUMN healthparameterids bigint[]

UPDATE activity SET healthparameterids=ARRAY[1,3,6] where id%3 = 1
UPDATE activity SET healthparameterids=ARRAY[4,5,6] where id%3 = 2
UPDATE activity SET healthparameterids=ARRAY[1,2,3] where id%3 = 0
