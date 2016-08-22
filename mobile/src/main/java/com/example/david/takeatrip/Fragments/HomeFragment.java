package com.example.david.takeatrip.Fragments;

import android.support.v4.app.Fragment;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
public class HomeFragment extends Fragment {

//    private static final String TAG = "TEST HomeFragment";
//
//    TextView nome;
//    TextView cognome;
//
//    private Context context;
//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
//
//    private ArrayList<DataObject> dataFollowers;
//
//
//    private ViewGroup group;
//    private ImageView image_default;
//
//
//
//    private ArrayList<Profilo> followers;
//
//    public HomeFragment(Context context, ArrayList<Profilo> listaSeguaci) {
//        this.context = context;
//        followers = listaSeguaci;
//        dataFollowers = new ArrayList<DataObject>();
//
//        for(Profilo p : followers){
//            dataFollowers.add(new DataObject(p));
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        Bundle args = getArguments();
//        View v = inflater.inflate(R.layout.activity_recyclerview_lista_viaggi, container, false);
//
//        if (args != null) {
//
//            mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
//            mRecyclerView.setHasFixedSize(true);
//            mLayoutManager = new LinearLayoutManager(context);
//            mRecyclerView.setLayoutManager(mLayoutManager);
//            mAdapter = new RecyclerViewViaggiAdapter(getDataSet(), this.getContext());
//            mRecyclerView.setAdapter(mAdapter);
//
//
//            Log.i(TAG, "context of the Followers Fragment: " + context);
//
//
//            image_default = new ImageView(context);
//            image_default.setImageResource((R.drawable.default_male));
//            group = new ViewGroup(context) {
//                @Override
//                protected void onLayout(boolean changed, int l, int t, int r, int b) {
//
//                }
//            };
//            group.addView(image_default);
//
//
//            Log.i(TAG, "data set: " + getDataSet());
//            RecyclerViewAdapterFollowers adapter = new RecyclerViewAdapterFollowers(getDataSet(), getContext());
//            adapter.onCreateViewHolder(group, 0);
//            mRecyclerView.setAdapter(adapter);
//        }
//
//
//        return v;
//    }
//
//
//
//    private ArrayList<DataObject> getDataSet() {
//        return dataFollowers;
//    }

}

