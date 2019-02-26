package nl.plff.plffserver.parcel.processor;

import nl.plff.plffserver.parcel.Parcel;

import java.io.IOException;
import java.util.HashMap;

/**
 * Processing unit for parcels.
 * Parcels can be registered here and executors will be called accordingly.
 */
public class ParcelUnit {

    private static ParcelUnit instance;
    private HashMap<Class, ParcelExecutor> executors;

    private ParcelUnit() {
        this.executors = new HashMap<>();
    }

    public static ParcelUnit getInstance() {
        if (instance == null) instance = new ParcelUnit();
        return instance;
    }

    /**
     * Register a parcel to the processing unit.
     * Example usage:
     * registerParcel(OkParcel.class, parcel -> System.out.println("Executor!"));
     * Every kind of parcel can only have one executor, one can, however, register superclasses and all corresponding
     * subclasses will also be redirected.
     *
     * @param parcelClass Kind of parcel to add an executor for
     * @param executor Executor to add
     */
    public void registerParcel(Class parcelClass, ParcelExecutor executor) {
        executors.put(parcelClass, executor);
    }

    public void processParcel(Parcel parcel) throws IOException, ClassNotFoundException {
        ParcelExecutor executor = executors.get(parcel.getClass());
        if (executor == null) {
            executor = executors.get(parcel.getClass().getSuperclass());
            if (executor == null) throw new ClassNotFoundException();
        }
        executor.onParcel(parcel);
    }

}
