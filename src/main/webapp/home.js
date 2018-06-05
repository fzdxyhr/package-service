/**
 * Created by hp on 2018/4/26.
 */
define(["home"], function (home) {
    return {
        template: `
          <div>
            <el-container>
                <el-header style="padding: 0;">
                    <div class="homepage">
                        <div class="logo">
                            <img src="image/logo.png"/>
                        </div>
                        <div class="logo-content">
                            <span class="spilt"></span>
                            <span class="text">多租户管理平台</span>
                        </div>
                        <div style="float: right;margin-top: 15px;margin-right:15px;cursor: pointer;">
                            <el-dropdown style="color:#cfd0d2!important;" trigger="click" @command="handleCommand">
                              <span class="el-dropdown-link">
                                {{loginName}}<i class="el-icon-arrow-down el-icon--right"></i>
                              </span>
                              <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item command="about">关于</el-dropdown-item>
                                <el-dropdown-item command="exit">退出</el-dropdown-item>
                              </el-dropdown-menu>
                            </el-dropdown>
                        </div>
                    </div>
                </el-header>
                <el-container>
                    <el-aside style="margin-top: 10px;width: auto!important;">
                        <el-menu :default-active="defaultActive" class="el-menu-vertical-demo" @select="handleSelect" @open="handleOpen" @close="handleClose"
                                 :collapse="isCollapse" background-color="#545c64"
                                 text-color="#fff"
                                 active-text-color="#ffd04b">
                            <el-menu-item index="1" @click="changeMenu">
                                <i class="el-icon-menu"></i>
                                <span slot="title">{{title}}</span>
                            </el-menu-item>
                            <el-menu-item index="2" @click="go2Content('/home/index')">
                                <i class="el-icon-news"></i>
                                <span slot="title">首页</span>
                            </el-menu-item>
                            <el-menu-item index="3" @click="go2Content('/home/tenant')">
                                <i class="el-icon-news"></i>
                                <span slot="title">租户</span>
                            </el-menu-item>

                        </el-menu>
                    </el-aside>
                    <el-main class="main" id="container">
                        <router-view></router-view>
                    </el-main>
                </el-container>
                <!--<el-footer style="margin-top: 10px;position: fixed;bottom: 0;">-->
                <!--footer-->
                <!--</el-footer>-->
            </el-container>
            <el-dialog
              title="确认"
              :visible.sync="dialogVisible"
              width="30%">
              <span>是否确定退出系统？</span>
              <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">否</el-button>
                <el-button type="primary" @click="exitConfirm">是</el-button>
              </span>
            </el-dialog>
          </div>
        `,
        data: function () {
            return {
                title: "收起",
                loginName: "",
                isCollapse: false,
                dialogVisible: false
            }
        },
        mounted: function () {
            this.showLoginName();
        },
        computed: {
            defaultActive: function () {
                return this.$store.state.adminleftnavnum;
            }
        },
        watch: {
            defaultActive(curVal, oldVal){
                localStorage.setItem("index", curVal);
            }
        },
        methods: {
            showLoginName: function () {
                let self = this;
                const userInfoState = JSON.parse(localStorage.getItem('userInfoState'));
                self.loginName = userInfoState.userInfo.user_name;
            },
            handleCommand: function (command) {
                let self = this;
                if ('exit' == command) {//退出做的动作
                    self.dialogVisible = true;
                }
                if ('about' == command) {//关于做的动作

                }
            },
            changeMenu: function () {
                let self = this;
                if (self.isCollapse) {
                    self.isCollapse = false;
                    self.title = "收起";
                } else if (!self.isCollapse) {
                    self.isCollapse = true;
                    self.title = "展开";
                }
                console.log(self.isCollapse)
            },
            exitConfirm: function () {
                localStorage.removeItem("userInfoState");//删除登录信息
                this.$router.push("/login");//跳转到首页
            },
            handleSelect: function (key, keyPath) {
                let self = this;
                if (key == 2 || key == 3) {
                    this.$store.state.adminleftnavnum = key;

                }
                console.log(key)
            },
            handleOpen: function (key, keyPath) {
                console.log(key, keyPath);
            },
            handleClose: function (key, keyPath) {
                console.log(key, keyPath);
            },
            go2Content: function (url) {
                this.$router.push(url);
            },
        }
    };
});