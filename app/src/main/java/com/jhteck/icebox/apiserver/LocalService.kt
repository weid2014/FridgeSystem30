package com.jhteck.icebox.apiserver

import android.util.Log
import com.google.gson.Gson
import com.hele.mrd.app.lib.base.BaseApp
import com.jhteck.icebox.api.*
import com.jhteck.icebox.repository.entity.AvailRfidEntity
import com.jhteck.icebox.repository.entity.MaterialBatchEntity
import com.jhteck.icebox.repository.entity.MaterialEntity
import com.jhteck.icebox.utils.DbUtil
import com.jhteck.icebox.utils.GetJsonDataUtil

/**
 * 从本地数据中加载数据
 */
class LocalService {
    companion object {
        /**
         * 模拟数据
         */
        fun mockDataToLocal(gson: Gson) {
            val availRfidDao = DbUtil.getDb().availRfidDao()
            val medicalDao = DbUtil.getDb().medicaldao()
            val materialBatchDao = DbUtil.getDb().materialBatchDao()
            var rfids = availRfidDao.getAll()
            //判断没有 则从json文件中读取一份
            if (rfids == null || rfids.isEmpty()) {//模拟數據
                val originData = GetJsonDataUtil().getJson(BaseApp.app, "rfid.json")
                val rfidDataGson = gson.fromJson(originData, RfidDao::class.java)
                for (data in rfidDataGson.results.avail_rfids) {
                    val availRfidEntity =
                        gson.fromJson(gson.toJson(data), AvailRfidEntity::class.java)
                    availRfidDao.insert(availRfidEntity);//插入到rfid表

                    val materialEntity = gson.fromJson(
                        gson.toJson(data.material),
                        MaterialEntity::class.java
                    )
                    materialEntity.id = data.id
                    medicalDao.insertAll(materialEntity); //插入到material药品表
                    val materialBatchEntity = gson.fromJson(
                        gson.toJson(data.material_batch),
                        MaterialBatchEntity::class.java
                    )
                    materialBatchEntity.id = data.id
                    materialBatchDao.insert(materialBatchEntity); //插入 material_batch 批号表
                }
            }
        }

        /**
         * 从本地加载
         */
        fun loadRfidsFromLocal(gson: Gson): RfidResults {
            val availRfidDao = DbUtil.getDb().availRfidDao()
            val medicalDao = DbUtil.getDb().medicaldao()
            val materialBatchDao = DbUtil.getDb().materialBatchDao()
            var rfids = availRfidDao.getAll();//获取rfid
            var availRfids = mutableListOf<AvailRfid>();

            for (rfid in rfids) {
                var availRfid = gson.fromJson(gson.toJson(rfid), AvailRfid::class.java);

                var medicalEntity = medicalDao.getById(rfid.id); //获取药品
                var material = gson.fromJson(gson.toJson(medicalEntity), Material::class.java);

                availRfid.material = material;

                var materialBatchEntity = materialBatchDao.getById(rfid.id);//获取批号
                var materialBatch =
                    gson.fromJson(gson.toJson(materialBatchEntity), MaterialBatch::class.java);

                availRfid.material_batch = materialBatch;

                availRfids.add(availRfid)//拼接数据
            }

            var result = RfidResults(availRfids, mutableListOf<String>());
            Log.d("lalala", "从本地数据库读取到result=" + result.avail_rfids.size)
            return result
        }

        /**
         * 数据保存到本地
         */
        fun realDataToLocal(rfidDao: RfidDao) {
            val gson = Gson()
            val availRfidDao = DbUtil.getDb().availRfidDao()
            val medicalDao = DbUtil.getDb().medicaldao()
            val materialBatchDao = DbUtil.getDb().materialBatchDao()
            for (data in rfidDao.results.avail_rfids) {
                val availRfidEntity =
                    gson.fromJson(gson.toJson(data), AvailRfidEntity::class.java)
                availRfidDao.insert(availRfidEntity);//插入到rfid表

                val materialEntity = gson.fromJson(
                    gson.toJson(data.material),
                    MaterialEntity::class.java
                )
                materialEntity.id = data.id
                medicalDao.insertAll(materialEntity); //插入到material药品表
                val materialBatchEntity = gson.fromJson(
                    gson.toJson(data.material_batch),
                    MaterialBatchEntity::class.java
                )
                materialBatchEntity.id = data.id
                materialBatchDao.insert(materialBatchEntity); //插入 material_batch 批号表
            }
        }

        /**
         * 删除本地某条记录
         */
        fun deleteDataToLocal(availRfidlist: List<AvailRfid>) {
            val gson = Gson()
            val availRfidDao = DbUtil.getDb().availRfidDao()
            val medicalDao = DbUtil.getDb().medicaldao()
            val materialBatchDao = DbUtil.getDb().materialBatchDao()
            for (data in availRfidlist) {
                val availRfidEntity =
                    gson.fromJson(gson.toJson(data), AvailRfidEntity::class.java)
                availRfidDao.delete(availRfidEntity);//插入到rfid表

                val materialEntity = gson.fromJson(
                    gson.toJson(data.material),
                    MaterialEntity::class.java
                )
                materialEntity.id = data.id
                medicalDao.delete(materialEntity); //插入到material药品表
                val materialBatchEntity = gson.fromJson(
                    gson.toJson(data.material_batch),
                    MaterialBatchEntity::class.java
                )
                materialBatchEntity.id = data.id
                materialBatchDao.delete(materialBatchEntity); //插入 material_batch 批号表
            }
        }

        /**
         * 增加一条本地某条记录
         */
        fun addDataToLocal(availRfidlist: List<AvailRfid>) {
            val gson = Gson()
            val availRfidDao = DbUtil.getDb().availRfidDao()
            val medicalDao = DbUtil.getDb().medicaldao()
            val materialBatchDao = DbUtil.getDb().materialBatchDao()
            for (data in availRfidlist) {
                val availRfidEntity =
                    gson.fromJson(gson.toJson(data), AvailRfidEntity::class.java)
                availRfidDao.insert(availRfidEntity);//插入到rfid表

                val materialEntity = gson.fromJson(
                    gson.toJson(data.material),
                    MaterialEntity::class.java
                )
                materialEntity.id = data.id
                medicalDao.insertAll(materialEntity); //插入到material药品表
                val materialBatchEntity = gson.fromJson(
                    gson.toJson(data.material_batch),
                    MaterialBatchEntity::class.java
                )
                materialBatchEntity.id = data.id
                materialBatchDao.insert(materialBatchEntity); //插入 material_batch 批号表
            }
        }


        /**
         * 批量更新本地记录
         */
        fun updateLocalAvailRfidBatch(datas: List<AvailRfid>) {
            for (data in datas){
                updateLocalAvailRfid(data)
            }
        }
        /**
         * 更新本地记录
         */
         fun updateLocalAvailRfid(data: AvailRfid) {
            val gson = Gson()
            val availRfidDao = DbUtil.getDb().availRfidDao()
            val medicalDao = DbUtil.getDb().medicaldao()
            val materialBatchDao = DbUtil.getDb().materialBatchDao()

            val availRfidEntity =
                gson.fromJson(gson.toJson(data), AvailRfidEntity::class.java)
            availRfidDao.update(availRfidEntity);//插入到rfid表

            val materialEntity = gson.fromJson(
                gson.toJson(data.material),
                MaterialEntity::class.java
            )
            materialEntity.id = data.id
            medicalDao.update(materialEntity); //插入到material药品表
            val materialBatchEntity = gson.fromJson(
                gson.toJson(data.material_batch),
                MaterialBatchEntity::class.java
            )
            materialBatchEntity.id = data.id
            materialBatchDao.update(materialBatchEntity); //插入 material_batch 批号表

        }

        /**
         * 单独更新AvailRfid
         */
        fun updateAvailRfidOnly(data: AvailRfid) {
            val gson = Gson()
            val availRfidDao = DbUtil.getDb().availRfidDao()
//            val medicalDao = DbUtil.getDb().medicaldao()
//            val materialBatchDao = DbUtil.getDb().materialBatchDao()

            val availRfidEntity =
                gson.fromJson(gson.toJson(data), AvailRfidEntity::class.java)
            availRfidDao.update(availRfidEntity);//插入到rfid表

//            val materialEntity = gson.fromJson(
//                gson.toJson(data.material),
//                MaterialEntity::class.java
//            )
//            materialEntity.id = data.id
//            medicalDao.update(materialEntity); //插入到material药品表
//            val materialBatchEntity = gson.fromJson(
//                gson.toJson(data.material_batch),
//                MaterialBatchEntity::class.java
//            )
//            materialBatchEntity.id = data.id
//            materialBatchDao.update(materialBatchEntity); //插入 material_batch 批号表

        }
        /**
         * 请空相关的表
         */
        fun cleanTable() {
            val availRfidDao = DbUtil.getDb().availRfidDao()
            val medicalDao = DbUtil.getDb().medicaldao()
            val materialBatchDao = DbUtil.getDb().materialBatchDao()
            availRfidDao.clean()
            medicalDao.clean();
            materialBatchDao.clean();

        }
    }
}