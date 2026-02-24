package com.kozen.support.x.model

import android.os.Parcel
import android.os.Parcelable
import java.lang.reflect.Method


class MethodInfo : Parcelable {
    private val className: String?
    private val methodName: String?
    private val paramTypeNames: Array<String?>? // 参数类型的全限定名

    constructor(method: Method) {
        this.className = method.getDeclaringClass().getName()
        this.methodName = method.getName()
        val paramTypes = method.getParameterTypes()
        this.paramTypeNames = arrayOfNulls<String>(paramTypes.size)
        for (i in paramTypes.indices) {
            paramTypeNames[i] = paramTypes[i]!!.getName()
        }
    }

    protected constructor(`in`: Parcel) {
        className = `in`.readString()
        methodName = `in`.readString()
        paramTypeNames = `in`.createStringArray()
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class)
    fun toMethod(): Method {
        val clazz = Class.forName(className)
        val paramTypes = arrayOfNulls<Class<*>>(paramTypeNames!!.size)
        for (i in paramTypeNames.indices) {
            paramTypes[i] = getClassFromName(paramTypeNames[i]!!)
        }
        return clazz.getDeclaredMethod(methodName, *paramTypes)
    }

    // 根据类型名称获取 Class，处理基本类型和数组
    @Throws(ClassNotFoundException::class)
    private fun getClassFromName(typeName: String): Class<*> {
        when (typeName) {
            "byte" -> return Byte::class.javaPrimitiveType!!
            "short" -> return Short::class.javaPrimitiveType!!
            "int" -> return Int::class.javaPrimitiveType!!
            "long" -> return Long::class.javaPrimitiveType!!
            "float" -> return Float::class.javaPrimitiveType!!
            "double" -> return Double::class.javaPrimitiveType!!
            "char" -> return Char::class.javaPrimitiveType!!
            "boolean" -> return Boolean::class.javaPrimitiveType!!
            else -> return Class.forName(typeName)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(className)
        dest.writeString(methodName)
        dest.writeStringArray(paramTypeNames)
    }

    companion object {
        // Parcelable 实现
        @JvmField
        val CREATOR: Parcelable.Creator<MethodInfo?> = object : Parcelable.Creator<MethodInfo?> {
            override fun createFromParcel(`in`: Parcel): MethodInfo {
                return MethodInfo(`in`)
            }

            override fun newArray(size: Int): Array<MethodInfo?> {
                return arrayOfNulls<MethodInfo>(size)
            }
        }
    }
}