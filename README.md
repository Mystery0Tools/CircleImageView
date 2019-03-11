# CircleImageView

![](https://img.shields.io/github/last-commit/Mystery0Tools/CircleImageView.svg) ![](https://img.shields.io/github/license/Mystery0Tools/CircleImageView.svg) ![](https://img.shields.io/github/stars/Mystery0Tools/CircleImageView.svg?label=Stars&style=social) ![](https://img.shields.io/github/forks/Mystery0Tools/CircleImageView.svg?label=Fork&style=social)

## 安装
![](https://img.shields.io/github/release/Mystery0Tools/CircleImageView.svg)

通过Gradle集成：

	implementation 'vip.mystery0.tools:circleimageview:{release-version}'
通过Maven集成：

	<dependency>
        <groupId>vip.mystery0.tools</groupId>
        <artifactId>circleimageview</artifactId>
  		<version>{release-version}</version>
  		<type>pom</type>
	</dependency>

## 如何使用

### 自定义控件
同样，sdk也提供自定义控件，你只需要将控件添加进布局中，无需处理相关逻辑，控件会自动请求数据并展示到控件上
**如果使用自定义控件的方式，那么不用手动调用初始化的方法，控件会自动调用**
```xml
<vip.mystery0.circleimageview.CircleImageView
		android:id="@+id/circleImageView"
		android:layout_width="wrap_content"
		android:layout_height="wrap_content"
		android:src="@drawable/ic_launcher_background" />
```
#### 自定义控件支持的自定义属性

|属性名|支持数据类型|备注|
| :------------ | :------------ | :------------ |
|additional_src|reference|额外图片的资源|
|draw_circle|boolean|是否将图片裁剪为圆形|
|draw_border|boolean|是否绘制边框|
|draw_additional|boolean|是否绘制额外图片|
|border_width|dimension|边框宽度|
|border_color|color|边框颜色|
|circle_radius|dimension|背景圆形的半径|
|center_x|float|圆心x的比例|
|center_y|float|圆形y的比例|
|additional_location|string|额外图片显示的位置|
|additional_width|dimension|额外图片的宽度|
|additional_height|dimension|额外图片的高度|
|additional_margin_horizontal|dimension|额外图片对边缘的横向间距|
|additional_margin_vertical|dimension|额外图片对边缘的纵向间距|

**设置新的格式后会自动刷新当前显示的格式，数据不会改变**

## 参考代码
[Sample](https://github.com/Mystery0Tools/CircleImageView/tree/master/app/src/main/java/vip/mystery0/circleimageview/demo "Sample")

## License
                                    
BSD 3-Clause "New" or "Revised" License
