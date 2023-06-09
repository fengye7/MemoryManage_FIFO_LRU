# MemoryManage_FIFO_LRU
1.	引言
>1.1 背景和目的
    在计算机系统中，内存管理是一个重要的问题，其中请求调页存储管理方式是一种常见的策略。它将物理内存分成固定大小的物理块和逻辑上相同大小的逻辑块，每个逻辑块被称为一页。当程序需要访问某一页时，该页会被调入物理内存中，如果物理内存已满，则需要使用页面置换算法将某些页替换出去，以便为新的页腾出空间。本实验旨在通过模拟作业执行过程，实现请求调页存储管理方式，并比较FIFO算法和LRU算法的效果，计算缺页率和页面置换次数，以验证这两种算法的优劣。
    
>1.2	实验环境和工具
本实验使用Java语言编写，开发环境为Eclipse IDE，实验在Windows 10操作系统的计算机上进行。

>1.3	简述问题
>>	基本任务
假设每个页面可存放10条指令，分配给一个作业的内存块为4。模拟一个作业的执行过程，该作业有320条指令，即它的地址空间为32页，目前所有页还没有调入内存。

>>	模拟过程
>>	在模拟过程中，如果所访问指令在内存中，则显示其物理地址，并转到下一条指令；如果没有在内存中，则发生缺页，此时需要记录缺页次数，并将其调入内存。如果4个内存块中已装入作业，则需进行页面置换。

>>	所有320条指令执行完成后，计算并显示作业执行过程中发生的缺页率。
>>	置换算法可以选用FIFO或者LRU算法
>>	作业中指令访问次序可以按照下面原则形成：  
>>	    50%的指令是顺序执行的，25%是均匀分布在前地址部分，25％是均匀分布在后地址部分。
