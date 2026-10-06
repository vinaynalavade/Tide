package com.tide.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tide.app.data.local.entity.ProductEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ProductDao_Impl implements ProductDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProductEntity> __insertionAdapterOfProductEntity;

  private final EntityDeletionOrUpdateAdapter<ProductEntity> __updateAdapterOfProductEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public ProductDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProductEntity = new EntityInsertionAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `products` (`id`,`addictionId`,`brand`,`productName`,`unit`,`price`,`pricingBasis`,`unitsPerBasis`,`unitCost`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        statement.bindString(3, entity.getBrand());
        statement.bindString(4, entity.getProductName());
        statement.bindString(5, entity.getUnit());
        statement.bindDouble(6, entity.getPrice());
        statement.bindString(7, entity.getPricingBasis());
        statement.bindDouble(8, entity.getUnitsPerBasis());
        statement.bindDouble(9, entity.getUnitCost());
      }
    };
    this.__updateAdapterOfProductEntity = new EntityDeletionOrUpdateAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `products` SET `id` = ?,`addictionId` = ?,`brand` = ?,`productName` = ?,`unit` = ?,`price` = ?,`pricingBasis` = ?,`unitsPerBasis` = ?,`unitCost` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        statement.bindString(3, entity.getBrand());
        statement.bindString(4, entity.getProductName());
        statement.bindString(5, entity.getUnit());
        statement.bindDouble(6, entity.getPrice());
        statement.bindString(7, entity.getPricingBasis());
        statement.bindDouble(8, entity.getUnitsPerBasis());
        statement.bindDouble(9, entity.getUnitCost());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM products";
        return _query;
      }
    };
  }

  @Override
  public Object insertProduct(final ProductEntity product,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfProductEntity.insertAndReturnId(product);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateProduct(final ProductEntity product,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfProductEntity.handle(product);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<ProductEntity> getProductForAddiction(final long addictionId) {
    final String _sql = "SELECT * FROM products WHERE addictionId = ? ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<ProductEntity>() {
      @Override
      @Nullable
      public ProductEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "productName");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfPricingBasis = CursorUtil.getColumnIndexOrThrow(_cursor, "pricingBasis");
          final int _cursorIndexOfUnitsPerBasis = CursorUtil.getColumnIndexOrThrow(_cursor, "unitsPerBasis");
          final int _cursorIndexOfUnitCost = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCost");
          final ProductEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpProductName;
            _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final double _tmpPrice;
            _tmpPrice = _cursor.getDouble(_cursorIndexOfPrice);
            final String _tmpPricingBasis;
            _tmpPricingBasis = _cursor.getString(_cursorIndexOfPricingBasis);
            final double _tmpUnitsPerBasis;
            _tmpUnitsPerBasis = _cursor.getDouble(_cursorIndexOfUnitsPerBasis);
            final double _tmpUnitCost;
            _tmpUnitCost = _cursor.getDouble(_cursorIndexOfUnitCost);
            _result = new ProductEntity(_tmpId,_tmpAddictionId,_tmpBrand,_tmpProductName,_tmpUnit,_tmpPrice,_tmpPricingBasis,_tmpUnitsPerBasis,_tmpUnitCost);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getProductForAddictionSync(final long addictionId,
      final Continuation<? super ProductEntity> $completion) {
    final String _sql = "SELECT * FROM products WHERE addictionId = ? ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, addictionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProductEntity>() {
      @Override
      @Nullable
      public ProductEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "productName");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfPricingBasis = CursorUtil.getColumnIndexOrThrow(_cursor, "pricingBasis");
          final int _cursorIndexOfUnitsPerBasis = CursorUtil.getColumnIndexOrThrow(_cursor, "unitsPerBasis");
          final int _cursorIndexOfUnitCost = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCost");
          final ProductEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpProductName;
            _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final double _tmpPrice;
            _tmpPrice = _cursor.getDouble(_cursorIndexOfPrice);
            final String _tmpPricingBasis;
            _tmpPricingBasis = _cursor.getString(_cursorIndexOfPricingBasis);
            final double _tmpUnitsPerBasis;
            _tmpUnitsPerBasis = _cursor.getDouble(_cursorIndexOfUnitsPerBasis);
            final double _tmpUnitCost;
            _tmpUnitCost = _cursor.getDouble(_cursorIndexOfUnitCost);
            _result = new ProductEntity(_tmpId,_tmpAddictionId,_tmpBrand,_tmpProductName,_tmpUnit,_tmpPrice,_tmpPricingBasis,_tmpUnitsPerBasis,_tmpUnitCost);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
