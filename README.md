## Java实现坦克大战 (简易图形界面)

----
## 游戏功能
#### 游戏预览

<img width="1000" src="assets/img/interface.gif"/>

#### 游戏控制
* 可以开始、暂停、继续、退出游戏、继续上局游戏。
* WSAD 或 ↑↓←→ 控制移动方向，支持斜向移动
* SPACE键 发射子弹
* ALT+SPACE键 发射超级子弹
* 游戏时间为2分钟，时间内没有剿灭敌方，或被对方剿灭，则游戏失败
* 按F2重置游戏
#### 游戏设置
* 坦克移动方向：上、下、左、右、左上、左下、右上、右下
* 敌方坦克可以随机移动，发射，改变方向
* 敌方坦克之间不会相撞，撞击到自己的队友时，立即改变方向
* 子弹发射，玩家坦克发射由人为按键控制发射
* 子弹击中墙壁时，子弹消失
* 坦克可以随机发射，敌方坦克间断的发射，间断时间在1-2秒之间随机，
* 子弹撞击坦克后，子弹销毁，坦克消失
* 显示爆炸效果
* 被敌方子弹击中后，敌方子弹销毁，玩家坦克掉血
* 双方坦克相撞，玩家坦克掉血
* 玩家血量为200，敌方血量为20，子弹攻击为10
* 可以发射超级子弹
* 设定游戏时间，时间内没有消灭完敌人，就游戏失败。
* 在窗口右上角会显示当前的游戏信息，包括玩家坦克剩余血量，敌方坦克剩余数量，杀死敌方数量，游戏剩余时间。
* 判断游戏输赢，用弹出菜单显示结果
* 重置游戏设置
* 实现继续上局游戏(IO)
* 设置障碍物地图：坦克不能穿越水体和墙体，子弹可以穿越水体，但不能穿越墙体
#### 待实现...
* 子弹击中鹰碉堡时,游戏结束
* 坦克有两种状态：无敌状态和非无敌状态
* 坦克刚复活时的5秒钟为无敌状态，此时对方的子弹对它无效。
* 当双方都为无敌状态时，相撞都不会死；
* 当双方坦克其中一方为无敌状态时，撞上它的对方坦克会死；
* 可以设置游戏的一些参数（包括玩家坦克的血量值，敌方坦克的总数量，同一时间敌方的坦克的最多数量、游戏时间。）

----
## 游戏实现
#### 核心思路
* 将 repaint() 放入线程中，每50毫秒调用一次 repaint()
* 将所有类的动态运动放入类自身的 draw() 方法中，在 repaint() 中统一调用，减少多线程阻塞
#### 抽象类型
* JFrame & JPanel：窗体 & 游戏界面 (图形界面非重点，简易显示)
* Tank类：需要实现 draw(), move(), fire() 方法
* Shell类；需要实现
* Bomb类：实现爆炸效果，需要实现 draw() 方法
* Recorder类：用于记录游戏信息
#### 注意事项
* 内存抖动：一些时间效率、空间效率没有作过多考虑
  * 在高频率执行的代码里使用new创建对象：因为敌人坦克是不停移动的，所以每个敌人坦克的区域是不断变化的。50毫秒执行一次，如果在这个方法里new出来几个对象，内存的变化是非常大的，所以，尽可能的不要在高频率执行的代码里使用new来创建对象，如果能用成员变量就用成员变量。
  * 没有思考如何管理资源能够使内存消耗更小：对于坦克、子弹、爆炸容器的管理，本程序中因为频繁的对容器的数据进行添加、移除操作，所以在容器的选择上，首选LinkedList<E>，因为LinkedList<E>的数据结构为链表，修改数据（添加、移除）效率比较高。