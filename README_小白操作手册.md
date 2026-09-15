# 📱 一劳永逸打包手册（小白版）

> 目标：**以后每次打包，包名自动唯一，永不 UID 冲突，点一下按钮出 APK。**
> 你只需要做一次配置，之后就是"换网页 → 点按钮 → 下载"。

---

## 一、这套东西是什么

```
webwrap/                      ← 整个工程（已帮你写好）
├── app/
│   ├── build.gradle          ← ★ 自动生成唯一包名的核心逻辑
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/.../MainActivity.java   ← 显示网页的代码
│       └── assets/web/       ← ★ 你的网页放这里！入口必须叫 index.html
├── .github/workflows/build.yml   ← 云端自动打包的配置
├── build.gradle
├── settings.gradle
└── gradle.properties
```

**你唯一要改的地方：`app/src/main/assets/web/` 里的网页文件。**

---

## 二、第一次配置（15 分钟，只做一次）

### 第 1 步：注册 GitHub（已有可跳过）

1. 手机/电脑浏览器打开 → `https://github.com`
2. 点 **Sign up**，用邮箱注册（免费）
3. 记住你的**用户名**（后面要用）

### 第 2 步：新建仓库

1. 登录后点右上角 **+** → **New repository**
2. 名字随意填，比如 `myapp`
3. 选 **Public**（免费账号私有仓库跑 Actions 也免费，但 Public 最简单）
4. 点 **Create repository**

### 第 3 步：上传本工程

**方法 A（手机，最简单）：**
1. 在仓库页面点 **Add file → Upload files**
2. 把 `webwrap` 文件夹里的**所有内容**拖进去
   - ⚠️ 注意：**不要**把 `webwrap` 这个外层文件夹拖进去，要拖**里面的文件**
   - 隐藏文件夹 `.github` 也要一起传（手机文件管理器要开"显示隐藏文件"）
3. 点 **Commit changes**

**方法 B（电脑，用 git 命令）：**
```bash
cd webwrap
git init
git add .
git commit -m "first commit"
git branch -M main
git remote add origin https://github.com/你的用户名/myapp.git
git push -u origin main
```

### 第 4 步：搞定"每次不同签名"的问题

⚠️ **重要**：默认情况下，每次构建会**当场生成新密钥**，这会导致**新包无法覆盖旧包安装**（得先卸载旧的）。

**如果你希望同一个 App 能覆盖升级**，就固定一个签名：

1. 电脑上生成一次密钥（或用在线工具生成 `.keystore`）
2. 转成 base64：
   ```bash
   # Windows PowerShell:
   [Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore"))
   ```
3. 在 GitHub 仓库 → **Settings → Secrets and variables → Actions**
4. 点 **New repository secret**，添加：
   | Name | Value |
   |------|-------|
   | `KEYSTORE_BASE64` | 上面那串 base64 |
   | `KS_PASS` | 你的密钥库密码 |
   | `KEY_ALIAS` | 你的别名（如 `mykey`） |
   | `KEY_PASS` | 你的密钥密码 |

> 💡 **不想搞签名？** 也行——每次出包都是全新 App，装的时候 App 名字会有点像一个新软件。对"自己用、每次独立"完全没问题。

**签名生成命令（有 Java 环境时）：**
```bash
keytool -genkeypair -v -keystore release.keystore \
  -alias mykey -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass 你的密码 -keypass 你的密码 \
  -dname "CN=Me, O=Personal, C=CN"
```

---

## 三、以后每次打包（10 秒，这才是重点）

### 场景 1：换了网页内容

1. 打开你的 GitHub 仓库
2. 点进 `app/src/main/assets/web/`
3. 点 **Add file → Upload files**，把新网页传上去（同名自动覆盖）
4. 点 **Commit changes**
5. **自动开始打包！** 等 2-3 分钟

### 场景 2：不改内容，只想再出一个新包名

1. 仓库页 → **Actions** 标签
2. 左边点 **Build APK**
3. 右边点 **Run workflow**
4. 填：
   - `应用名`：比如「我的工具箱」
   - `包名`：**留空** = 每次自动唯一 ✅（推荐）
5. 点绿色 **Run workflow**

### 下载 APK

1. 等右上角圆圈变绿 ✅（约 2-3 分钟）
2. 点进这次运行记录
3. 最下方 **Artifacts** 区域 → 点 **下载**
4. 得到 `.apk` → 传到手机安装（要允许"未知来源"）

---

## 四、常见问题

**Q：构建失败怎么办？**
A：点进失败的那次运行，把红色报错截图发我，我帮你看。

**Q：装的时候提示"应用未安装 / 解析包错误"？**
A：多半是签名不一致，先卸载旧版本再装。

**Q：能上架应用商店吗？**
A：可以，但需要固定签名 + 符合商店规范，到时候我帮你调。

**Q：我想改成每次手动指定包名？**
A：Run workflow 时在 `包名` 框里填，比如 `com.you.myapp`。

**Q：我的网页要联网调接口，能用吗？**
A：能。已开启 INTERNET 权限 + 允许明文 HTTP。但如果是 `file://` 跨域，可能要改成本地打包数据或加 CORS 头。

---

## 五、包名生成规则（了解即可）

```
com.mytools . a + 时间戳(年月日时分) + 4位随机hex
             例：com.mytools.a2609152239a3f9
```

改前缀：打开 `app/build.gradle` 第 12 行
```groovy
def basePrefix = "com.mytools"   // ← 改成你自己的，比如 com.zhangsan
```

---

## 六、检查清单 ✅

配置完成前，确认：
- [ ] GitHub 账号有了
- [ ] 仓库建好了
- [ ] 所有文件（含 `.github` 隐藏文件夹）都传上去了
- [ ] （可选）签名 Secrets 配好了
- [ ] 跑过一次，成功拿到 APK

**卡在哪一步，直接把截图发我。**