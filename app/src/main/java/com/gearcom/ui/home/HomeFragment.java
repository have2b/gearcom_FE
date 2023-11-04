package com.gearcom.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gearcom.ListAllProductFragment;
import com.gearcom.ProductDetailActivity;
import com.gearcom.ProductListActivity;
import com.gearcom.R;
import com.gearcom.adapters.CategoriesHomeAdapter;
import com.gearcom.adapters.CategoriesHomeInterface;
import com.gearcom.adapters.RecyclerViewAdapter;
import com.gearcom.adapters.RecyclerViewInterface;
import com.gearcom.api.Api;
import com.gearcom.model.Category;
import com.gearcom.model.Product;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private List<Product> productList;
    private List<Category> categoryList;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate((R.layout.fragment_home),container,false);

        // Khai báo TextView cần theo dõi sự kiện nhấn
        TextView textView = root.findViewById(R.id.viewAllProducts);
        TextView textView1 = root.findViewById(R.id.ProductCate);
        // Đặt OnClickListener cho TextView

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                // Mở Activity mới
                startActivity(intent);
            }
        });
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo instance mới của Fragment muốn chuyển đến
                Fragment fragment = new ListAllProductFragment();

                // Sử dụng FragmentManager và FragmentTransaction để thay thế Fragment hiện tại bằng Fragment mới
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);

                // (Tuỳ chọn) Thêm Fragment hiện tại vào back stack
                fragmentTransaction.addToBackStack(null);

                // Thực hiện thay thế Fragment
                fragmentTransaction.commit();
            }
        });

        RecyclerViewInterface recyclerViewInterface = new RecyclerViewInterface() {
            @Override
            public void onClickItem(int position) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                Product product = productList.get(position);
                intent.putExtra("product_selected", (Serializable) product);
                startActivity(intent);
            }
        };
        CategoriesHomeInterface categoriesHomeInterface = new CategoriesHomeInterface() {
            @Override
            public void onClickItem(int position) {

            }
        };
        Api.api.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.body() != null) {
                    categoryList = response.body();
                    // Xử lý danh sách sản phẩm ở đây
                    RecyclerView recyclerView = root.findViewById(R.id.cateRecyclerView);
                    CategoriesHomeAdapter adapter = new CategoriesHomeAdapter(getActivity(), categoryList, categoriesHomeInterface);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                } else {
                    Toast.makeText(getActivity(), "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        Api.api.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.body() != null) {
                    productList = response.body();
                    if (productList.size() > 5) {
                        productList = productList.subList(productList.size() - 5, productList.size());
                    }
                    // Xử lý danh sách sản phẩm ở đây
                    RecyclerView recyclerView = root.findViewById(R.id.mRecyclerView);
                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), productList, recyclerViewInterface);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                } else {
                    Toast.makeText(getActivity(), "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}