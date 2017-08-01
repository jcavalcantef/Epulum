package jhm.ufam.br.epulum.RVAdapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import jhm.ufam.br.epulum.Classes.BasicImageDownloader;
import jhm.ufam.br.epulum.Classes.CustomVolleyRequest;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.Classes.Receita;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ReceitaViewHolder> {

    public static class ReceitaViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView receitaNome;
        ImageView receitaFoto;

        ReceitaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            receitaNome = (TextView)itemView.findViewById(R.id.person_name);
            receitaFoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    List<Receita> receitas;

    public RVAdapter(List<Receita> receitas){
        this.receitas = receitas;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ReceitaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ReceitaViewHolder pvh = new ReceitaViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ReceitaViewHolder receitaViewHolder, int i) {
        receitaViewHolder.receitaNome.setText(receitas.get(i).getNome());
        try {
            receitaViewHolder.receitaFoto.setImageResource(receitas.get(i).getPhotoId());
        }catch (Exception e1){
            e1.printStackTrace();
        }
        try {

            Log.v("fotoLocal","existe?");
            Log.v("fotoLocal",""+receitas.get(i).getFotoLocal());
            //File f = new File("/My Device/Epulum/img_2017-07-31_16:59:17:000000.jpg");
            File f = new File(receitas.get(i).getFotoLocal());
            Log.v("fotoLocal",f.getAbsolutePath());
            if(f.exists()) {
                Log.v("fotoLocal","existe");
                //receitaViewHolder.receitaFoto.setImageURI(Uri.fromFile(f));
                //((BitmapDrawable)receitaViewHolder.receitaFoto.getDrawable()).getBitmap().recycle();
                //Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                Bitmap myBitmap = ShrinkBitmap(f.getAbsolutePath(), 300, 300);
                Log.v("fotoLocal",myBitmap.toString());
                receitaViewHolder.receitaFoto.setImageBitmap(myBitmap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    Bitmap ShrinkBitmap(String file, int width, int height){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }
    @Override
    public int getItemCount() {
        return receitas.size();
    }
}
