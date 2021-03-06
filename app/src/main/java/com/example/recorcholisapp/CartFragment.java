package com.example.recorcholisapp;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    private RecyclerView recyclerCart;
    private LinearLayout noProductsLinearLayout;
    private TextView totalAmountTextView;
    private CartAdapter mCartAdapter;
    public TextView currentTicketsText;
    public int updatedTickets;

    private Paint p = new Paint();

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_cart, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(container.getContext(), Login.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cards").child("data").push();

        recyclerCart = result.findViewById(R.id.recycler_cart_products);
        recyclerCart.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerCart.setHasFixedSize(true);
        noProductsLinearLayout = result.findViewById(R.id.linear_layout_no_products_instructions);
        totalAmountTextView = result.findViewById(R.id.text_cart_total_number);

        currentTicketsText = result.findViewById(R.id.text_cart_current_money_number);


        currentTicketsText.setText(Integer.toString(((MainActivity) getActivity()).currentTickets));

        Button readMoneyButton = result.findViewById(R.id.button_cart_read_card);
        readMoneyButton.setOnClickListener(view -> ((MainActivity)getActivity()).onReadMoney(readMoneyButton));

        Button payButton = result.findViewById(R.id.button_pay_cart_items);
        payButton.setOnClickListener(v -> {
            if (mCartAdapter != null && mCartAdapter.cartProducts.size() > 0) {
                LinkedList<Product> products = new LinkedList<>();
                int price = 0;
                for (int i = 1; i < ResourcesSingleton.getInstance().getCart().length; i++) {
                    if(ResourcesSingleton.getInstance().getCart()[i] > 0) {
                        products.add(ResourcesSingleton.getInstance().getProduct(i));
                        price += ResourcesSingleton.getInstance().getProduct(i).getPrice() * ResourcesSingleton.getInstance().getCart()[i];
                    }
                }

                totalAmountTextView.setText(Integer.toString(price));
                mCartAdapter = new CartAdapter(products);
                recyclerCart.setAdapter(mCartAdapter);
                if(price > ((MainActivity)(CartFragment.this.getActivity())).currentTickets) {
                    if(((MainActivity)(CartFragment.this.getActivity())).currentTickets == 0){
                        Toast.makeText(getActivity(), "No tienes tickets o intentalo de nuevo", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getActivity(), "No tienes tickets suficientes", Toast.LENGTH_LONG).show();
                    }

                } else {
                    //hacer cobro
                    updatedTickets = ((MainActivity)(CartFragment.this.getActivity())).currentTickets - price;
                    int depositTickets = -price;
                    ((MainActivity)(CartFragment.this.getActivity())).setDepositTickets(depositTickets);
                    ((MainActivity)(CartFragment.this.getActivity())).onDeposit(payButton);
                    currentTicketsText.setText(Integer.toString(updatedTickets));
                    saveUserInformation(container, updatedTickets, price);
                }

            } else {
                Toast.makeText(getActivity(), "Necesitas canjear al menos un producto", Toast.LENGTH_LONG).show();
            }
        });

        return result;
    }

    private void saveUserInformation(ViewGroup container, int updatedTickets, int price){
        Map<String,String> data = new HashMap<String, String>();
        //Map<String, Map<String, String>> card = new HashMap<String, Map<String, String>>();


        String uuid = ((MainActivity) getActivity()).hexUID;

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateF = formatter.format(date);

        data.put("Descripcion", "Compra");
        data.put("Fecha", dateF);
        data.put("Maquina", "Kiosco");
        data.put("Saldo", "0");
        data.put("Tickets", Integer.toString(price));

        //card.put(uuid, data);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();
        UserInformation userInformation = new UserInformation(uuid, userID, ((MainActivity) getActivity()).currentMoney, ((MainActivity) getActivity()).currentTickets, data);

        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(container.getContext(), "Información guardada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ResourcesSingleton.getInstance().getTotalQuantity() > 0) {
            LinkedList<Product> products = new LinkedList<>();
            int price = 0;
            for (int i = 1; i < ResourcesSingleton.getInstance().getCart().length; i++) {
                if(ResourcesSingleton.getInstance().getCart()[i] > 0) {
                    products.add(ResourcesSingleton.getInstance().getProduct(i));
                    price += ResourcesSingleton.getInstance().getProduct(i).getPrice() * ResourcesSingleton.getInstance().getCart()[i];
                }
            }

            totalAmountTextView.setText(Integer.toString(price));
            mCartAdapter = new CartAdapter(products);
            recyclerCart.setAdapter(mCartAdapter);

            currentTicketsText.setText(Integer.toString(((MainActivity) getActivity()).currentTickets));
            initSwipe();
            hideInstructions();
        } else {
            totalAmountTextView.setText("0");
            showInstructions();
        }
    }

    public void removeProducts() {
        if(mCartAdapter != null && recyclerCart != null) {
            currentTicketsText.setText(updatedTickets);
            mCartAdapter = new CartAdapter(new LinkedList<>());
            recyclerCart.setAdapter(mCartAdapter);
            showInstructions();
        }
    }


    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                mCartAdapter.removeItem(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#E44D43"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#E44D43"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerCart);
    }

    private void hideInstructions() {
        noProductsLinearLayout.setVisibility(View.GONE);
        recyclerCart.setVisibility(View.VISIBLE);
    }

    private void showInstructions() {
        noProductsLinearLayout.setVisibility(View.VISIBLE);
        recyclerCart.setVisibility(View.GONE);
    }


    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartRowController> {
        LinkedList<Product> cartProducts;

        CartAdapter(LinkedList<Product> cartProducts) {
            this.cartProducts = cartProducts;
        }

        @Override
        public CartRowController onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CartRowController(getActivity().getLayoutInflater().inflate(R.layout.row_cart_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(CartRowController holder, int position) {
            holder.bindModel(cartProducts.get(position));
        }

        @Override
        public int getItemCount() {
            return cartProducts.size();
        }

        void removeItem(int position) {
            // Retrieve product
            Product cartProduct = cartProducts.remove(position);

            // Update price
            int newPrice = Integer.parseInt(totalAmountTextView.getText().toString()) - cartProduct.getPrice() * ResourcesSingleton.getInstance().getCart()[cartProduct.getId()];

            ResourcesSingleton.getInstance().removeProduct(cartProduct.getId());

            totalAmountTextView.setText(Integer.toString(newPrice));
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartProducts.size());
            if (cartProducts.size() == 0) {
                showInstructions();
            }
        }

        class CartRowController extends RecyclerView.ViewHolder {
            SimpleDraweeView cartItemImage;
            TextView cartItemName;
            TextView cartItemPrice;
            ImageView cartImageProductRemove;
            TextView cartProductQuantityCounter;
            ImageView cartImageProductAdd;
            TextView cartItemDeleteText;

            CartRowController(View row) {
                super(row);

                this.cartItemImage = row.findViewById(R.id.cart_item_image);
                this.cartItemName = row.findViewById(R.id.cart_item_name);
                this.cartItemPrice = row.findViewById(R.id.cart_item_price);
                this.cartImageProductRemove = row.findViewById(R.id.image_product_remove);
                this.cartProductQuantityCounter = row.findViewById(R.id.text_product_quantity_counter);
                this.cartImageProductAdd = row.findViewById(R.id.image_product_add);
                this.cartItemDeleteText = row.findViewById(R.id.cart_item_delete_text);
                cartItemDeleteText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

                cartImageProductRemove.setOnClickListener(v -> removeProductPrice());
                cartImageProductAdd.setOnClickListener(v -> addProductPrice());
                cartItemDeleteText.setOnClickListener(v -> {
                    if(mCartAdapter != null){
                        mCartAdapter.removeItem(getAdapterPosition());
                    }
                });
            }

            void bindModel(Product cartProduct) {
                cartItemName.setText(cartProduct.getName());
                cartItemPrice.setText(Integer.toString(cartProduct.getPrice()));
                cartProductQuantityCounter.setText(String.valueOf(ResourcesSingleton.getInstance().getCart()[cartProduct.getId()]));
                Uri uri = Uri.parse(cartProduct.getPhoto());
                cartItemImage.setImageURI(uri);
            }

            private void removeProductPrice() {
                int quantity = Integer.parseInt(cartProductQuantityCounter.getText().toString()) - 1;
                if (quantity > 0) {
                    Product cartProduct = mCartAdapter.cartProducts.get(getAdapterPosition());
                    ResourcesSingleton.getInstance().updateQuantity(cartProduct.getId(), quantity);

                    int productPrice = (int) Double.parseDouble((cartItemPrice.getText().toString()).replaceAll("[^\\d.]", ""));

                    int oldTotal = (int) Double.parseDouble((totalAmountTextView.getText().toString()).replaceAll("[^\\d.]", ""));
                    int newTotal = oldTotal - productPrice;
                    totalAmountTextView.setText(String.valueOf(newTotal));


                    mCartAdapter.cartProducts.set(getAdapterPosition(), cartProduct);
                    mCartAdapter.notifyItemChanged(getAdapterPosition());

                    cartProductQuantityCounter.setText(String.valueOf(quantity));
                }
            }

            private void addProductPrice() {
                int quantity = Integer.parseInt(cartProductQuantityCounter.getText().toString()) + 1;
                Product cartProduct = mCartAdapter.cartProducts.get(getAdapterPosition());
                ResourcesSingleton.getInstance().updateQuantity(cartProduct.getId(), quantity);

                int productPrice = Integer.parseInt(cartItemPrice.getText().toString());

                int oldTotal = Integer.parseInt(totalAmountTextView.getText().toString());
                int newTotal = oldTotal + productPrice;
                totalAmountTextView.setText(String.valueOf(newTotal));


                mCartAdapter.cartProducts.set(getAdapterPosition(), cartProduct);
                mCartAdapter.notifyItemChanged(getAdapterPosition());

                cartProductQuantityCounter.setText(String.valueOf(quantity));
            }
        }
    }
}

