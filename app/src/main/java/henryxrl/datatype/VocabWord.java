package henryxrl.datatype;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Henry on 06/28/2014.
 */
public class VocabWord implements Parcelable{
	public long id;
	public String word;
	public String phonetic;
	public String transCN;
	public String transEN;
	public String sentCN;
	public String sentEN;
	public String synonym;
	public String antonym;

	public VocabWord()
	{
		id = -1L;
		word = "";
		phonetic = "";
		transCN = "";
		transEN = "";
		sentCN = "";
		sentEN = "";
		synonym = "";
		antonym = "";
	}

	public VocabWord(long i)
	{
		id = i;
		word = "";
		phonetic = "";
		transCN = "";
		transEN = "";
		sentCN = "";
		sentEN = "";
		synonym = "";
		antonym = "";
	}

	public VocabWord(long i, String w, String p, String tc, String te, String sc, String se, String s, String a)
	{
		id = i;
		word = w;
		phonetic = p;
		transCN = tc;
		transEN = te;
		sentCN = sc;
		sentEN = se;
		synonym = s;
		antonym = a;
	}

	public VocabWord(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public String toString() {
		return word + "\t" + phonetic + "\t" + transCN + "\t" + transEN + "\t" + sentCN + "\t" + sentEN + "\t" + synonym + "\t" + antonym;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// We just need to write each field into the
		// parcel. When we read from parcel, they
		// will come back in the same order
		dest.writeLong(id);
		dest.writeString(word);
		dest.writeString(phonetic);
		dest.writeString(transCN);
		dest.writeString(transEN);
		dest.writeString(sentCN);
		dest.writeString(sentEN);
		dest.writeString(synonym);
		dest.writeString(antonym);
	}

	private void readFromParcel(Parcel in) {
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		id = in.readLong();
		word = in.readString();
		phonetic = in.readString();
		transCN = in.readString();
		transEN = in.readString();
		sentCN = in.readString();
		sentEN = in.readString();
		synonym = in.readString();
		antonym = in.readString();
	}

	public static final Parcelable.Creator CREATOR =
			new Parcelable.Creator() {
				public VocabWord createFromParcel(Parcel in) {
					return new VocabWord(in);
				}
				public VocabWord[] newArray(int size) {
					return new VocabWord[size];
				}
			};
}
