@file:JvmName("EnumHelper")


/**
 * 项目状态
 */
enum class ProjectStatusEnum(val current: Int, val status: String?) {
    STARTED(0, "Started"),
    PENDING(1, "Pending"),
    FINISHED(2, "Finished"),
    ALL(3, null)
}

fun getStatusByCurrent(number: Int?): String? = ProjectStatusEnum.values().find { it.current == number }?.status

/**
 * 项目状态2
 */
enum class ProjectSectionStatusEnum(val current: Int, val status: String?) {
    STARTED(0, "0"),
    FINISHED(1, "1"),
    ALL(2, null)
}

fun getSectionStatusByCurrent(number: Int?): String? = ProjectSectionStatusEnum.values().find { it.current == number }?.status

/**
 * 项目状态1
 */
enum class ProjectStatueEnum(val status: String, val names: String?) {
    NOTSTART("NotStart", "待启动"),
    NOTSTARTED("NotStarted", "待启动"),
    STARTED("Started", "办理中"),
    PENDING("Pending", "挂起"),
    FINISHED("Finished", "已完结"),
    REMOVE("remove", "删除")
}

fun getNameByStatus(mStatus: String?): String? = ProjectStatueEnum.values().find { it.status == mStatus }?.names

/**
 * 项目状态对应的颜色
 */

enum class ProjectStatueColorEnum(val status: String, val color: String) {
    NOTSTART("NotStart", "#2BBD77"),
    NOTSTARTED("NotStarted", "#2BBD77"),
    STARTED("Started", "#0270C5"),
    PENDING("Pending", "#F0A432"),
    FINISHED("Finished", "#91979F"),
    REMOVE("Remove", "#FF000000"),
    REMOVED("Removed", "#FF000000")
}

fun getColorByStatus(mStatus: String?): String? = ProjectStatueColorEnum.values().find { it.status == mStatus }?.color

/**
 * 用户身份
 * @constructor
 */
enum class EnumMemberRole(val type: String, val department: String) {
    Agent("Agent", "待办人"),
    Manager("Enterprise-Manager", "企业负责人"),
    EnterpriseOperator("Enterprise-Operator", "企业经办人"),
    Contact("Department-Contact", "部门联系人"),
    DepartmentOperator("Department-Operator", "部门经办人"),
}

fun getMemberType(type: String): String? = EnumMemberRole.values().find { it.type == type }?.department


/**
 * 评价
 * @constructor
 */
enum class EvaluateStatus(val count: Int, val status: String) {
    DISSATISFACTIONS(0, "不满意"),
    DISSATISFACTION(1, "不满意"),
    BASIC_SATISFIED(2, "基本满意"),
    SATISFIED(3, "满意"),
    VERY_SATISFIED(4, "非常满意")
}

fun getEvaluateStatus(count: Int): String? = EvaluateStatus.values().find { it.count == count }?.status

enum class ProjectDetailEnum {
    NotStart,
    NotStarted,
    Started,
    Pending,
    Finished,
    Removed,
}

enum class PersonRoleEnum {
    Department,
    Agent,
    Enterprise
}
