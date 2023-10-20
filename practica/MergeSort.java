import java.util.ArrayList;

public class MergeSort<T extends Comparable<T>> {
    private ArrayList<T> arr;
    private SortingOrder so;
    public static enum SortingOrder{INCR, DECR};

    public MergeSort(ArrayList<T> arr, SortingOrder so) {
        this.arr = arr;
        this.so = so;
        msort(0, arr.size()-1);
    }

    public ArrayList<T> getResult() {
        return arr;
    }

    private void msort(int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;
            msort(l, m);
            msort(m+1, r);
            merge(l, m, r);
        }
    }

    private void merge(int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;

        ArrayList<T> leftArr = new ArrayList<T>();
        ArrayList<T> rightArr = new ArrayList<T>();

        for (int i = 0; i < n1; ++i)
            leftArr.add(arr.get(l+i));
        for (int i = 0; i < n2; ++i)
            rightArr.add(arr.get(m+i+1));

        int i = 0, j = 0;
        int k = l;
        switch (so) {
            case DECR:
                while (i < n1 && j < n2) {
                    if (leftArr.get(i).compareTo(rightArr.get(j)) > 0) {
                        arr.set(k, leftArr.get(i));
                        i++;
                    } else {
                        arr.set(k, rightArr.get(j));
                        j++;
                    } 
                    ++k;
                }
                break;
            default:
                while (i < n1 && j < n2) {
                    if (leftArr.get(i).compareTo(rightArr.get(j)) < 0) {
                        arr.set(k, leftArr.get(i));
                        i++;
                    } else {
                        arr.set(k, rightArr.get(j));
                        j++;
                    } 
                    ++k;
                }
        }
        
        while (i < n1) {
            arr.set(k, leftArr.get(i));
            i++; k++;
        }
        while (j < n2) {
            arr.set(k, rightArr.get(j));
            j++; k++;
        }
    }
}