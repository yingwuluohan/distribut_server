package com.self.transac.distribult_server.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistributTrancHandler extends ChannelInboundHandlerAdapter {


    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE );

    private Map< String , List<String >> transactionTypeMap = new ConcurrentHashMap<String, List<String>>();
    private Map<String , Boolean > isEndMap = new ConcurrentHashMap<>();
    private Map<String , Integer > transactionCountMap = new ConcurrentHashMap<>();

    @Override
    public void handlerAdded( ChannelHandlerContext ctx ){

        channelGroup.add( ctx.channel() );
    }


    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx , Object msg ){
        System.out.println( "接收数据：" + msg.toString());
        JSONObject jsonObject = JSON.parseObject( (String)msg );
        //create 创建事务组 ，add添加事务
        String command = jsonObject.getString( "command" );
        String groupId = jsonObject.getString( "groupId" );
        //子事务类型： commit -待提交 ， rollback -待回滚
        String transactionType = jsonObject.getString( "transactionType" );
        //事务数量
        Integer transactionCount = jsonObject.getInteger( "transactionCount" );
        //是否结束事务
        Boolean isEnd = jsonObject.getBoolean( "isEnd" );

        if( "create".equals( command )){
            //创建事务组
            transactionTypeMap.put( groupId , new ArrayList<>());
        }else if( "add".equals( command )){
            transactionTypeMap.get( groupId ).add( transactionType);
            if ( isEnd ){
                isEndMap.put( groupId , true );
                transactionCountMap.put( groupId , transactionCount );
            }

            JSONObject result = new JSONObject();
            result.put( "groupId" ,groupId );
            //如果已经接收到事务结束事务的标记，比较事务是否已经全部到达 ，如果已经全部到达看是否需要回滚
            if( isEndMap.get( groupId ) &&
                    transactionCountMap.get( groupId ).equals( transactionTypeMap.get( groupId).size())){
                if( transactionTypeMap.get( groupId).contains( "rollback" )){
                    result.put( "command" ,"rollback" );
                }else{
                    result.put( "command" ,"commit" );
                }
                sendResult( result );
            }

        }

    }

    private void sendResult( JSONObject result ){
//        for(Channel channel :channelGroup ){
//            System.out.println( "发送数据" );
//            channel.writeAndFlush( result.toJSONString() );
//        }
    }
    /** 重写父类的所有方法**/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println( "channelActive" );
        super.channelActive( ctx );
    }

    //注册
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        System.out.println( "channelRegistered" );
        super.channelRegistered( ctx );
    }
    /***/
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println( "channelInactive------" );
        super.channelInactive( ctx );
    }
    /***/
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println( "channelUnregistered------" );
        super.channelUnregistered( ctx );
    }
}
