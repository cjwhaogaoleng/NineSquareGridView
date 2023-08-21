# NineSquareGridView
## 效果展示
https://github.com/cjwhaogaoleng/NineSquareGridView/assets/117556474/22c01161-3f4b-47f6-8eb5-036c91ee918e

 ## 源码位置
/app/src/main/java/com/example/textcolorchange/NineSquareGridView.kt

 ## 代码讲解
  ### java
```
gv.setLockPatternListener(new NineSquareGridView.LockPatternListener() {
            @Override
            public void unLock(@NonNull String password) {
                if (!mPassword.equals(password)) {
                    gv.showSelectError();
                    Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();

                }
                else {
                    gv.postDelayed(()->gv.clearSelect(),1000);
                    Toast.makeText(MainActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void lock(@NonNull String password) {
                Log.d(TAG, "lock: " + password);
                mPassword = password;
                Toast.makeText(MainActivity.this, "密码设置成功", Toast.LENGTH_SHORT).show();
                gv.postDelayed(() ->
                    gv.clearSelect(), 1000);
            }
        });
```
接口回调unLock和lock方法，lock方法传回设置的密码，unlock传回解锁时的密码
 ## 实现方法
 ```
//清除所有已经绘画的点，恢复默认
fun clearSelect()
//将所有已选择的点变成错误状态
fun showSelectError()
```
 ## 待完成
 - [x] 自定义view
   - [x] onMeasure 源码和写法基本了解
   - [x] onDraw 源码和写法基本了解
   - [x] onTouch 事件分发，处理，拦截基本了解，源码仔细看过
 - [ ] compose 已经接触，还没有另一种熟练
 - [ ] :disappointed: :blush:
