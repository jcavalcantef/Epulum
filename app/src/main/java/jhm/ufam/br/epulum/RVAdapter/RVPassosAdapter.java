package jhm.ufam.br.epulum.RVAdapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jhm.ufam.br.epulum.R;

/**
 * Created by Mateus on 21/06/2017.
 */


public class RVPassosAdapter extends RecyclerView.Adapter<RVPassosAdapter.PassosViewHolder> {

    public static class PassosViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView txt_num;
        TextView txt_pass;

        public PassosViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_passos);
            txt_num = (TextView)itemView.findViewById(R.id.txt_num_passo);
            txt_pass = (TextView)itemView.findViewById(R.id.txt_passo);
        }
    }

    List<String> passos;

    public RVPassosAdapter(List<String> pass){
        this.passos = pass;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PassosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_passos, viewGroup, false);
        PassosViewHolder pvh = new PassosViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVPassosAdapter.PassosViewHolder passosViewHolder, int i) {
        passosViewHolder.txt_pass.setText("  "+passos.get(i).toString());
        passosViewHolder.txt_num.setText(i+1+"");


    }

    @Override
    public int getItemCount() {
        return passos.size();
    }

    public void addPasso(String passo){
        passos.add(passo);
        notifyDataSetChanged();
    }
}

