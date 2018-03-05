import config.Services

object Params {

    object Session {
        const val TABLE_NAME = "session"
        const val SESSION_ID = "id"
        const val PATIENT_CF = "patientcf"
        const val LEADER_CF = "leadercf"
        const val START_DATE = "startdate"
        const val END_DATE = "enddate"
        const val INSTANCE_ID = "microserviceinstanceid"
    }

    object Activity {
        const val TABLE_NAME = "activity"
        const val ID = "id"
        const val NAME = "name"
        const val ACTIVITY_TYPE_ID = "activitytypeid"
        const val ACRONYM = "acronym"
        const val HEALTH_PARAMETER_IDS = "healthparameterids"
    }

    object ActivityType {
        const val TABLE_NAME = "activitytype"
        const val ID = "id"
        const val NAME = "name"
    }

    object Boundary {
        const val TABLE_NAME = "boundary"
        const val ID = "id"
        const val HEALTH_PARAMETER_ID = "healthparameterid"
        const val UPPERBOUND = "upperbound"
        const val LOWERBOUND = "lowerbound"
        const val LIGHT_WARNING_OFFSET = "lightwarning_offset"
        const val STATUS = "status"
        const val ITS_GOOD = "itsgood"
        const val MIN_AGE = "minage"
        const val MAX_AGE = "maxage"
    }

    object HealthParameter {
        const val TABLE_NAME = "healthparameter"
        const val ID = "id"
        const val NAME = "name"
        const val ACRONYM = "acronym"
    }

    object Log {
        const val TABLE_NAME = "log"
        const val ID = "id"
        const val NAME = "name"
        const val LOG_TIME = "logtime"
        const val HEALTH_PARAMETER_ID = "healthparameterid"
        const val HEALTH_PARAMETER_VALUE = "healthparametervalue"
        const val SESSION_ID = "sessionid"
    }

    object Operator {
        const val TABLE_NAME = "operator"
        const val ID = "id"
        const val NAME = "name"
        const val SURNAME = "surname"
        const val ROLE_ID = "roleid"
        const val IS_ACTIVE = "isactive"
    }

    object Role {
        const val TABLE_NAME = "role"
        const val ID = "id"
        const val NAME = "name"
    }

    object Task {
        const val TABLE_NAME = "augmentedTask"
        const val ID = "id"
        const val OPERATOR_CF = "operatorcf"
        const val START_TIME = "starttime"
        const val END_TIME = "endtime"
        const val ACTIVITY_ID = "activityid"
        const val STATUS_ID = "statusid"
        const val SESSION_ID = "sessionid"
    }

    object TaskStatus {
        const val TABLE_NAME = "taskstatus"
        const val ID = "id"
        const val NAME = "name"
    }
}

object Connection {
    val LOCAL_HOST = "127.0.0.1"
    val REMOTE_HOST = "2.234.121.101"
    val ADDRESS = LOCAL_HOST
    val PROTOCOL = Services.Utils.Protocols.http
    val API = "api"
    val PROTOCOL_SEPARATOR = "://"
    val PORT_SEPARATOR =":"
}
