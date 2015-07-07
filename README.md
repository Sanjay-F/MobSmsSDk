
这是一个使用mob的短信注册sdk的demo.
但区别于官方版本的是这个使用的是xml+真正的activity。
这样可以快速的集成到你的项目里面去！！！
而且界面可以自己定制了。

###主界面
![github](https://github.com/Sanjay-F/MobSmsSDk/blob/master/app/src/main/res/drawable-xhdpi/mx_f.png "github")
###选择国家列表
![github](https://github.com/Sanjay-F/MobSmsSDk/blob/master/app/src/main/res/drawable-xhdpi/mx_s.png "github")

待解决的问题
1.目前哪些国家码还是通过 SMSSDK.getSupportedCountries(); 的形式来拿的。
 下次就直接把取回的数据保存到本地，这样就再也不用去联网等待了！！
 (目前他们的做法是，把所有国家列表都存在本地smssdk_arrays.xml里面，在countryListview显示，然后通过网络去获取实际支持的国家列表,通过两者比较，来确定是否支持！！所以你可以看到下面这段代码
 
     @Override
    public void onItemClick(GroupListView parent, View view, int group, int position) {
        if (position >= 0) {
            String[] country = lvCountry.getCountry(group, position);
            if (countryRules != null && countryRules.containsKey(country[1])) {
                Toast.makeText(this, "code=" + country[1], Toast.LENGTH_SHORT).show();
                String countryCode = country[1];
                setResult(RESULT_OK, SignUpActivity.makeResultIntent(countryCode));
                finish();
            } else {
                Toast.makeText(this, "暂时不支持", Toast.LENGTH_SHORT).show();
            }
        }
    }
 
 //countryRules就是通过网络获取的数据。
 //String[] country = lvCountry.getCountry(group, position);这部分数据是写死在里面的
 
 
 ）
 
2.他的那个CountryListview看着有点让人不开心啊，下次再稍微调整下。


