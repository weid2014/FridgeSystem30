package com.jhteck.icebox.repository

//@Database(entities = [User::class], version = 1, exportSchema = false)
//abstract class KotlinAppDataBase : RoomDatabase(){
//    companion object {
//        private const val DATA_NAME = "kotlin_db"
//
//        @Volatile
//        private var INSTANCE: KotlinAppDataBase? = null
//
//        /**
//         * 双重校验锁单例,返回数据库实例
//         */
//        fun getDataBase(): KotlinAppDataBase = INSTANCE ?: synchronized(this) {
//            val instance = INSTANCE ?: Room
//                .databaseBuilder(ContextUtils.getApplicationContext(), KotlinAppDataBase::class.java, DATA_NAME)
//                .build().also {
//                    INSTANCE = it
//                }
//            instance
//        }
//    }
//    /**
//     * 返回 UserDao Dao对象
//     */
//    abstract fun userDao(): UserDao;
//}