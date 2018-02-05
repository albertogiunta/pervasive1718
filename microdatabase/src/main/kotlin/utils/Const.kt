object Params {

    object Activity {
        const val TABLE_NAME = "activity"
        const val ID = "id"
        const val NAME = "name"
        //        const val EXPECTED_EFFECT = "expectedeffect"
        const val ACTIVITY_TYPE_ID = "activitytypeid"
        const val ACRONYM = "acronym"
        const val STATUS_ID = "statusid"
    }

    object ActivityType {
        const val TABLE_NAME = "activitytype"
        const val ID = "id"
        const val NAME = "name"
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
        const val VALUE = "value"
        const val HEALTH_PARAMETER_ID = "healthparameterid"
        const val HEALTH_PARAMETER_VALUE = "healthparametervalue"
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

    object Boundary {
        const val TABLE_NAME = "boundary"
        const val ID = "id"
        const val HEALTH_PARAMETER_ID = "healthparameterid"
        const val ACTIVITY_ID = "activityid"
        const val UPPERBOUND = "upperbound"
        const val LOWERBOUND = "lowerbound"
    }

    object Task {
        const val TABLE_NAME = "task"
        const val ID = "id"
        const val OPERATOR_ID = "operatorid"
        const val START_TIME = "starttime"
        const val END_TIME = "endtime"
        const val ACTIVITY_ID = "activityid"
        const val TASK_STATUS_ID = "taskstatusid"
    }

    object TaskStatus {
        const val TABLE_NAME = "taskstatus"
        const val ID = "id"
        const val NAME = "name"
    }
}

object Connection{
    const val LOCAL_HOST = "127.0.0.1"
    const val REMOTE_HOST = "2.234.121.101"
    const val ADDRESS = LOCAL_HOST
    const val PROTOCOL = "http"
    const val API_PORT = "8080"
    const val POST = "POST"
    const val GET = "GET"
    const val PROTOCOL_SEPARATOR = "://"
    const val PORT_SEPARATOR =":"
}
