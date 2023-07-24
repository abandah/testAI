package com.ubitc.popuppush.models


class PermissionObj(
    var permissionManifest: String? = null,
    var permissionType :Int
) {

    data class Builder(var permissionManifest: String) {
        private var title: String? = null
        private var imageResourceId: Int? = null
        private var message: String? = null
        private var explanationMessage: String? = null

        private var isRequired: Boolean = false
        private var permissionType :Int = 0

        fun setTitle(title: String) = apply { this.title = title }

        fun setImageResourceId(imageResourceId: Int?) =
            apply { this.imageResourceId = imageResourceId }

        fun setMessage(message: String?) = apply { this.message = message }
        fun setExplanationMessage(explanationMessage: String?) =
            apply { this.explanationMessage = explanationMessage }

        fun setRequired(isRequired: Boolean) = apply { this.isRequired = isRequired }
        fun setPermissionType(permissionType: Int) = apply { this.permissionType = permissionType }
        fun build(): PermissionObj {
            return PermissionObj(
                permissionManifest,
                permissionType
            )
        }
    }

//    fun toPermissionModel(context: Context): PermissionModel {
//        val builder = PermissionModelBuilder.withContext(context)
//        builder.withCanSkip(false)
//        builder.withPermissionName(permissionManifest!!)
//        builder.withTitle(title!!)
//        builder.withMessage(message!!)
//        builder.withExplanationMessage(explanationMessage!!)
//        builder.withFontType("my_font.ttf")
//        builder.withLayoutColorRes(R.color.box_background)
//        builder.withImageResourceId(imageResourceId!!)
//        val permissionModel =  builder.build()
//        return permissionModel!!
//
//    }

}