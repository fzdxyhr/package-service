/**
 * Created by hp on 2018/4/25.
 */
define(['login'], function (login) {
    return {
        template: `
        <div class="loginBody">
          <div class="login">
            <el-row>
                <h1>租户平台</h1>
            </el-row>
            <el-row>
                <el-input id="name"  v-model="name" placeholder="请输入帐号">
                    <template slot="prepend">帐号</template>
                </el-input>
             </el-row>
             <el-row>
                <el-input id="password" v-model="password" type="password" placeholder="请输入密码">
                    <template slot="prepend">密码</template>
                </el-input>
             </el-row>
             <el-row>
                <el-button id="login" @click="check" style="width:50%" type="primary">登录</el-button>
             </el-row>
          </div>
          </div>
        `,
        data: function () {
            return {
                password: "",
                name: "",
                userInfo: {}
            }
        },
        methods: {
            check: function () {
                let self = this;
                //获取值
                var name = self.name;
                var password = self.password;
                if (name == '' || password == '') {
                    this.$message({
                        message: '账号或密码为空！',
                        type: 'error'
                    })
                    return;
                }
                if (!(name == 'admin' && password == 'admin')) {
                    this.$message({
                        message: '账号或密码错误！',
                        type: 'error'
                    })
                    return;
                }
                self.userInfo.user_name = name;
                self.userInfo.user_account = name;
                const userInfo = self.userInfo;
                localStorage.setItem('userInfoState', JSON.stringify({
                    userInfo,
                    timestamp: new Date().getTime()
                }));
                localStorage.setItem("index", "2");
                this.$router.push("/home/index");//跳转到首页
            }
        }
    };
});