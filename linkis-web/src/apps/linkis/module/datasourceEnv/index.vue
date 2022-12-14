<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
-->

<template>
  <div>
    <Row class="search-bar" type="flex">
      <Col span="6">
        <Input v-model="searchName" clearable suffix="ios-search" class="input" placeholder="搜索"></Input>
      </Col>
      <Col span="3">
        <Button type="primary" class="Button" @click="load()">{{
          $t('message.linkis.search')
        }}
        </Button>
        <Button type="success" class="Button" style="margin-left: 10px" @click="onAdd()">{{
          $t('message.linkis.basedata.add')
        }}
        </Button>
      </Col>
      <Col span="15">
      </Col>
    </Row>
    <div style="height: 600px">
      <Table border size="small" align="center" :columns="tableColumnNum" :data="pageDatalist" max-height="420"
        class="table-content">
        <template slot-scope="{ row,index }" slot="action">
          <ButtonGroup size="small">
            <Button
              :disabled="row.expire"
              size="small"
              type="primary"
              @click="onTableEdit(row, index)"
            >{{ $t('message.linkis.edit') }}
            </Button
            >
            <Button
              :disabled="row.expire"
              size="small"
              type="primary"
              @click="onTableDelete(row, index)"
            >
              {{ $t('message.linkis.basedata.remove') }}
            </Button>
          </ButtonGroup>
        </template>
      </Table>
    </div>
    <Modal
      width="800"
      class="modal"
      v-model="modalShow"
      :title="modalAddMode=='add' ? $t('message.linkis.basedata.add') : $t('message.linkis.basedata.edit')"
      :loading="modalLoading"
    >
      <div slot="footer">
        <Button type="text" size="large" @click="onModalCancel()">取消</Button>
        <Button type="primary" size="large" @click="onModalOk('userConfirm')">确定</Button>
      </div>
      <ErrorCodeForm ref="errorCodeForm" :data="modalEditData"></ErrorCodeForm>
    </Modal>
    <div style="margin: 10px; overflow: hidden; textAlign: center">
      <div>
        <Page
          :page-size="page.pageSize"
          :total="page.totalSize"
          :current="page.pageNow"
          @on-change="changePage"
          size="small"
          show-total
          show-elevator
          :prev-text="$t('message.linkis.previousPage')" :next-text="$t('message.linkis.nextPage')"
        ></Page>
      </div>
    </div>
  </div>
</template>
<script>
import mixin from '@/common/service/mixin';
import ErrorCodeForm from './EditForm/index'
import {add, del, edit, getList, getAllEnv} from "./service";
import {formatDate} from "iview/src/components/date-picker/util";
export default {
  mixins: [mixin],
  components: {ErrorCodeForm},
  data() {
    return {
      searchName: "",
      page: {
        totalSize: 0,
        pageSize: 10,
        pageNow: 1,
      },
      tableColumnNum: [
        {
          title: "ID",
          key: 'id',
          width: 100,
          tooltip: true,
          align: 'center',
        },
        {
          title: "环境名称",
          key: 'envName',
          minWidth: 50,
          tooltip: true,
          align: 'center',
        },
        {
          title: "环境描述",
          key: 'envDesc',
          tooltip: true,
          align: 'center',
        },
        {
          title: "数据源名称",
          key: 'name',
          tooltip: true,
          align: 'center',
        },
        {
          title: "参数",
          key: 'parameter',
          tooltip: true,
          align: 'center',
        },
        {
          title: "创建时间",
          key: 'createTime',
          minWidth: 50,
          tooltip: true,
          align: 'center',
          render: (h,params)=>{
            return h('div',
              formatDate(new Date(params.row.createTime),'yyyy-MM-dd hh:mm')
            )
          }
        },
        {
          title: "创建者",
          key: 'create_user',
          tooltip: true,
          align: 'center',
        },
        {
          title: "更新时间",
          key: 'updateTime',
          minWidth: 50,
          tooltip: true,
          align: 'center',
          render: (h,params)=>{
            return h('div',
              formatDate(new Date(params.row.createTime),'yyyy-MM-dd hh:mm')
            )
          }
        },
        {
          title: "更新者",
          key: 'modify_user',
          tooltip: true,
          align: 'center',
        },
        {
          title: this.$t('message.linkis.datasource.action'),
          width: 150,
          slot: 'action',
          align: 'center',
        },

      ],
      pageDatalist: [],
      allEnv: [],
      modalShow: false,
      modalAddMode: 'add',
      modalEditData: {
        createTime: '',
        createUser: '',
        datasourceTypeId: '',
        envDesc: '',
        envName: '',
        id: '',
        modifyTime: '',
        modifyUser: '',
        parameter: '',
        _index: '',
        _rowKey: ''
      },
      modalLoading: false
    };
  },
  created() {
    this.load()
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.load();
    },
    load() {
      let params = {
        searchName: this.searchName,
        currentPage: this.page.pageNow,
        pageSize: this.page.pageSize
      }
      getAllEnv().then((res) => {
        this.allEnv = [...res.typeList]
        this.allEnv.sort((a, b) => a.id - b.id)
        getList(params).then((data) => {
          this.pageDatalist = data.list.list
          this.page.totalSize = data.list.total
          let options = []
          //console.log(this.pageDatalist)
          this.pageDatalist.map(item => { item.name = this.allEnv[item.datasourceTypeId - 1].name})
          this.allEnv.map(item => {
            options.push({value: +item.id, label: item.name})
          })
          this.$refs['errorCodeForm'].changeSelector(options)
        })
      })
    },
    changePage(value) {
      this.page.pageNow = value
      this.load()
    },
    onAdd(){
      this.clearForm();
      this.modalAddMode = 'add'
      this.modalShow = true
    },
    onTableEdit(row){
      row.keytab = JSON.parse(row.parameter).keytab ? true : false;
      this.modalEditData = {...row}
      //console.log(this.modalEditData)
      this.modalAddMode = 'edit'
      this.modalShow = true
    },
    onTableDelete(row){

      this.$Modal.confirm({
        title: "提示信息",
        content: "确认是否删除该记录?",
        onOk: ()=>{
          let params = {
            id: row.id
          }
          del(params).then((data)=>{
            if(data.result) {
              this.$Message.success({
                duration: 3,
                content: "删除成功"
              })
            }else{
              this.$Message.success({
                duration: 3,
                content: "删除失败"
              })
            }
          })
          this.load()
        }
      })

    },
    clearForm(){
      for(let key in this.modalEditData) {
        this.modalEditData[key] = ''
      }
      this.modalEditData.keytab = false;
    },
    onModalOk(){
      this.$refs.errorCodeForm.formModel.submit((formData)=>{
        if('keytab' in formData) delete formData['keytab'];
        if('pic' in formData) delete formData['pic'];
        this.modalLoading = true
        formData.parameter = JSON.stringify(formData.parameter)
        if(this.modalAddMode=='add') {
          add(formData).then((data)=>{
            //console.log(data)
            if(data.result) {
              this.$Message.success({
                duration: 3,
                content: "添加成功"
              })
            }else{
              this.$Message.success({
                duration: 3,
                content: "添加失败"
              })
            }
          })
        }else {
          edit(formData).then((data)=>{
            //console.log(data)
            if(data.result) {
              this.$Message.success({
                duration: 3,
                content: "编辑成功"
              })
              this.load()
            }else{
              this.$Message.success({
                duration: 3,
                content: "编辑失败"
              })
            }
          })
        }
        this.modalLoading=false
        this.modalShow = false
      })
    },
    onModalCancel(){
      this.modalLoading=false
      this.modalShow = false
    }
  },
};
</script>

<style lang="scss" src="./index.scss" scoped>
</style>
