package henryxrl.datatype;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Henry on 06/28/2014.
 */
public class VocabBook implements Parcelable {
	public long id;
	public String name;
	public ArrayList<VocabList> list;

	public VocabBook()
	{
		id = -1L;
		name = "";
		list = new ArrayList<VocabList>();
	}

	public VocabBook(long i)
	{
		id = i;
		name = "";
		list = new ArrayList<VocabList>();
	}

	public VocabBook(long i, String n, ArrayList<VocabList> l)
	{
		id = i;
		name = n;
		list = l;
	}

	public VocabBook(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// The writeParcel method needs the flag
		// as well - but that's easy.
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeList(list);
		//dest.writeParcelable(list, flags);
	}

	public void readFromParcel(Parcel in) {
		// readParcelable needs the ClassLoader
		// but that can be picked up from the class
		// This will solve the BadParcelableException
		// because of ClassNotFoundException
		id = in.readLong();
		name = in.readString();
		list = in.readArrayList(VocabList.class.getClassLoader());
	}

	public static final Parcelable.Creator CREATOR =
			new Parcelable.Creator() {
				public VocabBook createFromParcel(Parcel in) {
					return new VocabBook(in);
				}
				public VocabBook[] newArray(int size) {
					return new VocabBook[size];
				}
			};

}
