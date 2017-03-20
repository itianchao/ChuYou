package sortListView;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.biz.KickMemberBiz;
import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.view.CircleImageView;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<SortModel> list = null;
	private Context mContext;
	private   Handler handler;
	private CircleEntity entity;

	public SortAdapter(Context mContext, List<SortModel> list, Handler handler, CircleEntity entity) {
		this.mContext = mContext;
		this.list = list;
		this.handler=handler;
		this.entity=entity;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.sort_item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.xian=(ImageView)view.findViewById(R.id.xian);
			viewHolder.KickedOutTextView=(TextView)view.findViewById(R.id.KickedOutTextView);
			viewHolder.HeadImage=(CircleImageView)view.findViewById(R.id.HeadImage);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
			viewHolder.xian.setVisibility(View.GONE);
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
			viewHolder.xian.setVisibility(View.VISIBLE);
		}
		/**判断是会否为圈主*/
		if(!entity.getIs_host().equals("0")){
			if(list.get(position).getOwner_id().equals(list.get(position).getUid())){
				viewHolder.KickedOutTextView.setVisibility(View.GONE);
			}else{
				viewHolder.KickedOutTextView.setVisibility(View.VISIBLE);
			}
		}else{
			viewHolder.KickedOutTextView.setVisibility(View.GONE);
		}
		ImageLoaderUtil.ImageLoader(list.get(position).getAvatar(),viewHolder.HeadImage,R.drawable.head_portrait);
		viewHolder.tvTitle.setText(this.list.get(position).getName());

		viewHolder.KickedOutTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击踢出
				new KickMemberBiz(mContext,handler,entity.getCid(),list.get(position).getUid(),position);
			}
		});
		return view;

	}



	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView xian;//灰线
		TextView KickedOutTextView;//踢出
		CircleImageView HeadImage;//头像
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 *
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}