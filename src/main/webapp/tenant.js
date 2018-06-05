/**
 * @author yhr
 * @date 2018/4/25
 * @version latest
 */

define(['tenant'], function (tenant) {
    return {
        template: `
            <div class="tenant">
                <el-form :inline="true" :model="formModel" class="demo-form-inline">
                    <el-form-item label="租户登录账号">
                        <el-input size="small" v-model="formModel.login_account" placeholder="租户登录账号"></el-input>
                    </el-form-item>
                    <el-form-item label="企业名称">
                        <el-input size="small" v-model="formModel.company_name" placeholder="企业名称"></el-input>
                    </el-form-item>
                    <el-form-item label="租户类型">
                        <el-select size="small" v-model="formModel.tenant_type" placeholder="租户类型">
                            <el-option v-for="item in tenantTypes" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="创建时间">
                        <el-date-picker size="small"
                                        v-model="formModel.start_time"
                                        type="datetime"
                                        placeholder="选择日期时间"
                                        align="right"
                                        :picker-options="pickerOptions">
                        </el-date-picker>
                        至
                        <el-date-picker size="small"
                                        v-model="formModel.end_time"
                                        type="datetime"
                                        placeholder="选择日期时间"
                                        align="right"
                                        :picker-options="pickerOptions">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" size="small" @click="search">搜索</el-button>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" size="small" @click="reset">重置</el-button>
                    </el-form-item>
                </el-form>
                <div class="contentBody">
                    <el-table :data="tableData" style="width: 100%;border: solid 1px rgb(215,215,215)">
                        <el-table-column prop="login_name" align="center" label="租户登录名称" width="180">
                        </el-table-column>
                        <el-table-column prop="company_name" align="center" label="企业名称" width="180">
                        </el-table-column>
                        <el-table-column align="center" label="租户类型">
                            <div slot-scope="scope">
                                <span v-if="scope.row.tenant_type ==0">正式用户</span>
                                <span v-else>非正式用户</span>
                            </div>
                        </el-table-column>
                        <el-table-column align="center" label="租户状态">
                            <div slot-scope="scope">
                                <span v-if="scope.row.tenant_status ==0">启用</span>
                                <span v-else>不启用</span>
                            </div>
                        </el-table-column>
                        <el-table-column prop="address" align="center" label="操作">
                            <template slot-scope="scope">
                                <el-button @click="login(scope.row)" type="text" size="medium">
                                    登录
                                </el-button>
                                <el-button @click="updateConfirm(scope.row)" type="text" size="medium">
                                    修改
                                </el-button>
                                <el-button @click="deleteTenant(scope.row)" type="text" size="medium">
                                    删除
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination style="float: right;margin-top: 10px;"
                                   @size-change="handleSizeChange"
                                   @current-change="handleCurrentChange"
                                   :current-page="formModel.pageNo"
                                   :page-sizes="pageSizes"
                                   :page-size="formModel.pageSize"
                                   layout="total, sizes, prev, pager, next, jumper"
                                   :total="total">
                    </el-pagination>

                </div>
                <el-dialog title="修改用户信息" :visible.sync="dialogFormVisible" width="37%">
                    <div>
                        <el-form :model="updateModel">
                            <el-form-item label="企业名称" :label-width="formLabelWidth">
                              <el-input width="260px" style="margin-left: 15px;" v-model="updateModel.company_name" auto-complete="off"></el-input>
                            </el-form-item>
                            <el-form-item label="租户类型" :label-width="formLabelWidth">
                              <el-select style="margin-left: 15px;" v-model="updateModel.tenant_type" placeholder="租户类型">
                                <el-option v-for="item in tenantTypes" :label="item.label" :value="item.value"></el-option>
                              </el-select>
                            </el-form-item>
                        </el-form>
                    </div>
                    <div slot="footer" class="dialog-footer">
                        <el-button @click="dialogFormVisible = false">取 消</el-button>
                        <el-button type="primary" @click="update">修 改</el-button>
                    </div>
                </el-dialog>
                <el-dialog
                      title="删除确认"
                      :visible.sync="deleteDialogVisible"
                      width="30%">
                      <div>是否确定删除该租户？</div>
                      <div slot="footer" class="dialog-footer">
                        <el-button @click="deleteDialogVisible = false">取 消</el-button>
                        <el-button type="primary" @click="deleteTenant">确 定</el-button>
                      </div>
                </el-dialog>
            </div>
        `,
        data: function () {
            return {
                pageNo: 1,
                pageSizes: [10, 20, 50, 100],
                pageSize: 1,
                total: 1,
                dialogFormVisible: false,
                deleteDialogVisible: false,
                formLabelWidth: '120px',
                //表格当前页数据
                tableData: [],
                dialogTitle: "",
                //0 升级，1删除
                deleteOrUpdate: 0,
                tenantTypes: [
                    {
                        "label": "所有用户",
                        "value": ""
                    },
                    {
                        "label": "非正式用户",
                        "value": "1"
                    },
                    {
                        "label": "正式用户",
                        "value": "0"
                    }
                ],
                formModel: {
                    login_account: "",
                    company_name: "",
                    tenant_type: "",
                    start_time: "",
                    end_time: "",
                    page_no: 1,
                    page_size: 10
                },
                updateModel: {
                    id: 0,
                    company_name: "",
                    tenant_type: "",
                },
                isCollapse: false,
                title: "收起",
                pickerOptions: {
                    shortcuts: [{
                        text: '今天',
                        onClick: function (picker) {
                            picker.$emit('pick', new Date());
                        }
                    },
                        {
                            text: '昨天',
                            onClick: function (picker) {
                                const date = new Date();
                                date.setTime(date.getTime() - 3600 * 1000 * 24);
                                picker.$emit('pick', date);
                            }
                        },
                        {
                            text: '一周前',
                            onClick: function (picker) {
                                const date = new Date();
                                date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
                                picker.$emit('pick', date);
                            }
                        }]
                }
            }
        },
        mounted: function () {
            this.search();
        },
        methods: {
            changeMenu: function () {
                let self = this;
                if (self.isCollapse) {
                    self.isCollapse = false;
                    self.title = "收起";
                } else if (!self.isCollapse) {
                    self.isCollapse = true;
                    self.title = "展开";
                }
            },
            handleSizeChange: function (val) {
                let self = this;
                self.formModel.page_size = val;
                self.search();
            },
            handleCurrentChange: function (val) {
                let self = this;
                self.formModel.page_no = val;
                self.search();
            },
            handleOpen: function (key, keyPath) {
                console.log(key, keyPath);
            },
            handleClose: function (key, keyPath) {
                console.log(key, keyPath);
            },
            reset: function () {
                let self = this;
                self.formModel = {
                    login_account: "",
                    company_name: "",
                    tenant_type: "",
                    start_time: "",
                    end_time: "",
                    page_no: 1,
                    page_size: 10
                }
            },
            search: function () {
                let self = this;
                var param = self.buildParam(self.formModel);
                this.$http.get('/api/v0.1/tenants?' + param).then(function (response) {
                    // 响应成功回调
                    var message = JSON.parse(response.bodyText);
                    self.tableData = message.data.items;
                    self.total = message.data.total;
                }, function (response) {
                    console.log(response);
                });
            },
            buildParam: function (item) {
                var param = "";
                var objProperty = Object.keys(item);
                objProperty.forEach(function (x, index) {
                    if (objProperty.length > 1) {
                        param += (x + "=" + item[x] + "&");
                    } else {
                        param += (x + "=" + item[x]);
                    }
                });
                if (param.length > 0) {
                    param = param.substring(0, param.length - 1);
                }
                return param;
            },
            login: function () {

            },
            update: function () {
                let self = this;
                this.$http.put('/api/v0.1/tenants/' + self.updateModel.id, self.updateModel).then(function (response) {
                    // 响应成功回调
                    var message = JSON.parse(response.bodyText);
                    self.dialogFormVisible = false;
                    if (message.code == 200) {
                        this.$message({
                            type: 'success',
                            message: '修改租户成功!'
                        });
                    }
                    self.search();
                }, function (response) {
                    var message = JSON.parse(response.bodyText);
                    this.$message({
                        type: 'error',
                        message: message.message
                    });
                });
            },
            deleteTenant: function (row) {
                let self = this;
                this.$confirm('此操作将永久删除该租户, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$http.delete('/api/v0.1/tenants/' + row.id).then(function (response) {
                        // 响应成功回调
                        var message = JSON.parse(response.bodyText);
                        if (message.code == 200) {
                            this.$message({
                                type: 'success',
                                message: '删除租户成功!'
                            });
                        }
                        self.search();
                    }, function (response) {
                        var message = JSON.parse(response.bodyText);
                        this.$message({
                            type: 'error',
                            message: message.message
                        });
                    });
                }).catch(() => {
                });
            },
            updateConfirm: function (row) {
                let self = this;
                self.updateModel.id = row.id;
                self.updateModel.company_name = row.company_name;
                self.updateModel.tenant_type = row.tenant_type;
                self.dialogFormVisible = true;
            },
            deleteConfirm: function (row) {
                let self = this;
                self.updateModel.id = row.id;
                self.deleteDialogVisible = true;
            }
        }
    }
});