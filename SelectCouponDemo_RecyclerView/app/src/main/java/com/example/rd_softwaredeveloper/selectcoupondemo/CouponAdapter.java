package com.example.rd_softwaredeveloper.selectcoupondemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
public class CouponAdapter  extends RecyclerView.Adapter<CouponAdapter.CouponVH>
{
    private List<TestBean> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;

    private int mSelectedPos = -1;//实现单选  方法二，变量保存当前选中的position

    private RecyclerView mRv;//实现单选方法三： RecyclerView另一种定向刷新方法：

    public CouponAdapter(List<TestBean> datas, Context context, RecyclerView rv)
    {
        mDatas = datas;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mRv = rv;

        //实现单选方法二： 设置数据集时，找到默认选中的pos
        for (int i = 0; i < mDatas.size(); i++)
        {
            if (mDatas.get(i).isSelected())
            {
                mSelectedPos = i;
            }
        }
    }

    @Override
    public CouponVH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new CouponVH(mInflater.inflate(R.layout.item_coupon, parent, false));
    }

    @Override
    public void onBindViewHolder(final CouponVH holder, final int position)
    {
        Log.d("TAG", "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        holder.ivSelect.setSelected(mDatas.get(position).isSelected());
        holder.tvCoupon.setText(mDatas.get(position).getName());

        holder.ivSelect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //实现单选，第一种方法，十分简单， Lv Rv通用,因为它们都有notifyDataSetChanged()方法
                // 每次点击时，先将所有的selected设为false，并且将当前点击的item 设为true， 刷新整个视图
/*                for (TestBean data : mDatas) {
                    data.setSelected(false);
                }
                mDatas.get(position).setSelected(true);
                notifyDataSetChanged();*/
                //实现单选方法二： notifyItemChanged() 定向刷新两个视图
                //如果勾选的不是已经勾选状态的Item
/*                if (mSelectedPos!=position){
                    //先取消上个item的勾选状态
                    mDatas.get(mSelectedPos).setSelected(false);
                    notifyItemChanged(mSelectedPos);
                    //设置新Item的勾选状态
                    mSelectedPos = position;
                    mDatas.get(mSelectedPos).setSelected(true);
                    notifyItemChanged(mSelectedPos);
                }*/

                //实现单选方法三： RecyclerView另一种定向刷新方法：不会有白光一闪动画 也不会重复onBindVIewHolder
                CouponVH couponVH = (CouponVH) mRv.findViewHolderForLayoutPosition(mSelectedPos);
                if (couponVH != null)
                {
                    //还在屏幕里
                    couponVH.ivSelect.setSelected(false);
                }
                else
                {
                    //add by 2016 11 22 for 一些极端情况，holder被缓存在Recycler的cacheView里，
                    //此时拿不到ViewHolder，但是也不会回调onBindViewHolder方法。所以add一个异常处理
                    notifyItemChanged(mSelectedPos);
                }
                mDatas.get(mSelectedPos).setSelected(false);//不管在不在屏幕里 都需要改变数据
                //设置新Item的勾选状态
                mSelectedPos = position;
                mDatas.get(mSelectedPos).setSelected(true);
                holder.ivSelect.setSelected(true);

                //实现单选方法四：
//                if (mSelectedPos != position)
//                {
//                    //先取消上个item的勾选状态
//                    mDatas.get(mSelectedPos).setSelected(false);
//                    //传递一个payload
//                    Bundle payloadOld = new Bundle();
//                    payloadOld.putBoolean("KEY_BOOLEAN", false);
//                    notifyItemChanged(mSelectedPos, payloadOld);
//                    //设置新Item的勾选状态
//                    mSelectedPos = position;
//                    mDatas.get(mSelectedPos).setSelected(true);
//                    Bundle payloadNew = new Bundle();
//                    payloadNew.putBoolean("KEY_BOOLEAN", true);
//                    notifyItemChanged(mSelectedPos, payloadNew);
//                }

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mContext.startActivity(new Intent(mContext, MainActivity.class));
            }
        });
    }

    @Override
    public void onBindViewHolder(CouponVH holder, int position, List<Object> payloads)
    {
        Log.d("TAG", "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "], payloads = [" + payloads + "]");
        if (payloads.isEmpty())
        {
            onBindViewHolder(holder, position);
        }
        else
        {
            Bundle payload = (Bundle) payloads.get(0);
            if (payload.containsKey("KEY_BOOLEAN"))
            {
                boolean aBoolean = payload.getBoolean("KEY_BOOLEAN");
                holder.ivSelect.setSelected(aBoolean);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return null != mDatas ? mDatas.size() : 0;
    }

    public static class CouponVH extends RecyclerView.ViewHolder
    {
        private ImageView ivSelect;
        private TextView tvCoupon;

        public CouponVH(View itemView)
        {
            super(itemView);
            ivSelect = (ImageView) itemView.findViewById(R.id.ivSelect);
            tvCoupon = (TextView) itemView.findViewById(R.id.tvCoupon);
        }
    }
}
