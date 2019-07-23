package com.self.transac.distribult_server.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

//SimpleChannelInboundHandler
public class DistributTrancHandler extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup  channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE );

    /** 存放 ： */
    private Map< String , List<String >> transactionTypeMap = new ConcurrentHashMap<String, List<String>>();
    private Map<String , Boolean > isEndMap = new ConcurrentHashMap<>();
    /** 存放： */
    private Map<String , Integer > transactionCountMap = new ConcurrentHashMap<>();



    @Override
    public void handlerAdded( ChannelHandlerContext ctx ){

        channelGroup.add( ctx.channel() );
    }

    /** 客户端连接服务端时候执行的方法 **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println( "----------------客户端连接服务端:-------" + ctx.channel().remoteAddress() );
        super.channelActive( ctx );
    }
    /** 读取客户端传递过来的数据 */
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx , Object msg ){
        System.out.println( "事务IP:" + ctx.channel().remoteAddress() );
        JSONObject jsonObject = JSON.parseObject( msg.toString() );
        //TODO create 创建事务组 ，add添加事务
        String command = jsonObject.getString( "commond" );
        String groupId = jsonObject.getString( "groupId" );
        //TODO 子事务类型： commit -待提交 ， rollback -待回滚
        String transactionType = jsonObject.getString( "transactionType" );
        //TODO 事务数量
        Integer transactionCount = jsonObject.getInteger( "transactionCount" );
        //TODO 是否结束事务
        Boolean isEnd = jsonObject.getBoolean( "isEnd" );

        if( "create".equals( command )){
            //创建事务组
            System.out.println( "创建事务组时的groupId是：" + groupId );
            transactionTypeMap.put( groupId , new ArrayList<>());
        }else if( "add".equals( command )){
            List<String > list = transactionTypeMap.get( groupId );
            if( null == list ){
                list = new ArrayList<>();
            }
            list.add( transactionType );
            transactionTypeMap.put( groupId , list );
            if ( isEnd ){
                isEndMap.put( groupId , true );
                transactionCountMap.put( groupId , transactionCount != null? transactionCount:0 );
            }

            JSONObject result = new JSONObject();
            result.put( "groupId" ,groupId );
            //如果已经接收到事务结束事务的标记，比较事务是否已经全部到达 ，如果已经全部到达看是否需要回滚
            System.out.println( "-------事务已提交的数量-----："+transactionCountMap.get( groupId ) );
            System.out.println( "-------事务的数量----："+list.size() );

            if( isEndMap.get( groupId ) &&transactionCountMap.get( groupId ).equals( list.size()) ){
                if( list.contains( "rollback" )){
                    result.put( "command" ,"rollback" );
                    sendResult( result , null );
                }else{

                    result.put( "command" ,"commit" );
                    sendResult( result ,ctx.channel() );
                }

            }
            System.out.println( "事务IP:" + ctx.channel().remoteAddress() + ",数据是--------------：" + result );
        }
    }
    private void sendResult( JSONObject result ,Channel channel ){
        System.out.println( "channelGroup是--------------：" + channelGroup );

        if( null != channel ){
            channel.writeAndFlush( result.toJSONString() );
        }else{
            for(Channel channelClients :channelGroup ){
                System.out.println( "rollback事务当前客户端连接是:" +channelClients.remoteAddress() );
                channelClients.writeAndFlush( result.toJSONString() );
            }
        }


    }
    @Override
    public boolean acceptInboundMessage( Object msg) throws  Exception{
        System.out.println( "server acceptInboundMessage------------------接收数据：" + msg.toString());

        return true;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("---------------server exception ------------------");
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println( "server channelRead0------------------接收数据：" +  msg.toString() );

    }




    //注册
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        System.out.println( "server begin register------------------ channelRegistered" );
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
