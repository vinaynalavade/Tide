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
import com.tide.app.data.local.entity.AddictionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AddictionDao_Impl implements AddictionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AddictionEntity> __insertionAdapterOfAddictionEntity;

  private final EntityDeletionOrUpdateAdapter<AddictionEntity> __updateAdapterOfAddictionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public AddictionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAddictionEntity = new EntityInsertionAdapter<AddictionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `addictions` (`id`,`name`,`category`,`unit`,`customUnit`,`baselineAmount`,`baselineFrequency`,`baselineCost`,`currency`,`goalType`,`startDate`,`active`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AddictionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCategory());
        statement.bindString(4, entity.getUnit());
        if (entity.getCustomUnit() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCustomUnit());
        }
        statement.bindDouble(6, entity.getBaselineAmount());
        statement.bindString(7, entity.getBaselineFrequency());
        statement.bindDouble(8, entity.getBaselineCost());
        statement.bindString(9, entity.getCurrency());
        statement.bindString(10, entity.getGoalType());
        statement.bindLong(11, entity.getStartDate());
        final int _tmp = entity.getActive() ? 1 : 0;
        statement.bindLong(12, _tmp);
      }
    };
    this.__updateAdapterOfAddictionEntity = new EntityDeletionOrUpdateAdapter<AddictionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `addictions` SET `id` = ?,`name` = ?,`category` = ?,`unit` = ?,`customUnit` = ?,`baselineAmount` = ?,`baselineFrequency` = ?,`baselineCost` = ?,`currency` = ?,`goalType` = ?,`startDate` = ?,`active` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AddictionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCategory());
        statement.bindString(4, entity.getUnit());
        if (entity.getCustomUnit() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCustomUnit());
        }
        statement.bindDouble(6, entity.getBaselineAmount());
        statement.bindString(7, entity.getBaselineFrequency());
        statement.bindDouble(8, entity.getBaselineCost());
        statement.bindString(9, entity.getCurrency());
        statement.bindString(10, entity.getGoalType());
        statement.bindLong(11, entity.getStartDate());
        final int _tmp = entity.getActive() ? 1 : 0;
        statement.bindLong(12, _tmp);
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM addictions";
        return _query;
      }
    };
  }

  @Override
  public Object insertAddiction(final AddictionEntity addiction,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAddictionEntity.insertAndReturnId(addiction);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAddiction(final AddictionEntity addiction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAddictionEntity.handle(addiction);
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
  public Flow<AddictionEntity> getActiveAddiction() {
    final String _sql = "SELECT * FROM addictions WHERE active = 1 ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"addictions"}, new Callable<AddictionEntity>() {
      @Override
      @Nullable
      public AddictionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCustomUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "customUnit");
          final int _cursorIndexOfBaselineAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineAmount");
          final int _cursorIndexOfBaselineFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineFrequency");
          final int _cursorIndexOfBaselineCost = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineCost");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfGoalType = CursorUtil.getColumnIndexOrThrow(_cursor, "goalType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final AddictionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCustomUnit;
            if (_cursor.isNull(_cursorIndexOfCustomUnit)) {
              _tmpCustomUnit = null;
            } else {
              _tmpCustomUnit = _cursor.getString(_cursorIndexOfCustomUnit);
            }
            final double _tmpBaselineAmount;
            _tmpBaselineAmount = _cursor.getDouble(_cursorIndexOfBaselineAmount);
            final String _tmpBaselineFrequency;
            _tmpBaselineFrequency = _cursor.getString(_cursorIndexOfBaselineFrequency);
            final double _tmpBaselineCost;
            _tmpBaselineCost = _cursor.getDouble(_cursorIndexOfBaselineCost);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final String _tmpGoalType;
            _tmpGoalType = _cursor.getString(_cursorIndexOfGoalType);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            _result = new AddictionEntity(_tmpId,_tmpName,_tmpCategory,_tmpUnit,_tmpCustomUnit,_tmpBaselineAmount,_tmpBaselineFrequency,_tmpBaselineCost,_tmpCurrency,_tmpGoalType,_tmpStartDate,_tmpActive);
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
  public Object getActiveAddictionSync(final Continuation<? super AddictionEntity> $completion) {
    final String _sql = "SELECT * FROM addictions WHERE active = 1 ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AddictionEntity>() {
      @Override
      @Nullable
      public AddictionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCustomUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "customUnit");
          final int _cursorIndexOfBaselineAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineAmount");
          final int _cursorIndexOfBaselineFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineFrequency");
          final int _cursorIndexOfBaselineCost = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineCost");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfGoalType = CursorUtil.getColumnIndexOrThrow(_cursor, "goalType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final AddictionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCustomUnit;
            if (_cursor.isNull(_cursorIndexOfCustomUnit)) {
              _tmpCustomUnit = null;
            } else {
              _tmpCustomUnit = _cursor.getString(_cursorIndexOfCustomUnit);
            }
            final double _tmpBaselineAmount;
            _tmpBaselineAmount = _cursor.getDouble(_cursorIndexOfBaselineAmount);
            final String _tmpBaselineFrequency;
            _tmpBaselineFrequency = _cursor.getString(_cursorIndexOfBaselineFrequency);
            final double _tmpBaselineCost;
            _tmpBaselineCost = _cursor.getDouble(_cursorIndexOfBaselineCost);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final String _tmpGoalType;
            _tmpGoalType = _cursor.getString(_cursorIndexOfGoalType);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            _result = new AddictionEntity(_tmpId,_tmpName,_tmpCategory,_tmpUnit,_tmpCustomUnit,_tmpBaselineAmount,_tmpBaselineFrequency,_tmpBaselineCost,_tmpCurrency,_tmpGoalType,_tmpStartDate,_tmpActive);
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

  @Override
  public Flow<List<AddictionEntity>> getAllAddictions() {
    final String _sql = "SELECT * FROM addictions ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"addictions"}, new Callable<List<AddictionEntity>>() {
      @Override
      @NonNull
      public List<AddictionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCustomUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "customUnit");
          final int _cursorIndexOfBaselineAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineAmount");
          final int _cursorIndexOfBaselineFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineFrequency");
          final int _cursorIndexOfBaselineCost = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineCost");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfGoalType = CursorUtil.getColumnIndexOrThrow(_cursor, "goalType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final List<AddictionEntity> _result = new ArrayList<AddictionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AddictionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCustomUnit;
            if (_cursor.isNull(_cursorIndexOfCustomUnit)) {
              _tmpCustomUnit = null;
            } else {
              _tmpCustomUnit = _cursor.getString(_cursorIndexOfCustomUnit);
            }
            final double _tmpBaselineAmount;
            _tmpBaselineAmount = _cursor.getDouble(_cursorIndexOfBaselineAmount);
            final String _tmpBaselineFrequency;
            _tmpBaselineFrequency = _cursor.getString(_cursorIndexOfBaselineFrequency);
            final double _tmpBaselineCost;
            _tmpBaselineCost = _cursor.getDouble(_cursorIndexOfBaselineCost);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final String _tmpGoalType;
            _tmpGoalType = _cursor.getString(_cursorIndexOfGoalType);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            _item = new AddictionEntity(_tmpId,_tmpName,_tmpCategory,_tmpUnit,_tmpCustomUnit,_tmpBaselineAmount,_tmpBaselineFrequency,_tmpBaselineCost,_tmpCurrency,_tmpGoalType,_tmpStartDate,_tmpActive);
            _result.add(_item);
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
  public Object getAddictionById(final long id,
      final Continuation<? super AddictionEntity> $completion) {
    final String _sql = "SELECT * FROM addictions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AddictionEntity>() {
      @Override
      @Nullable
      public AddictionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCustomUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "customUnit");
          final int _cursorIndexOfBaselineAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineAmount");
          final int _cursorIndexOfBaselineFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineFrequency");
          final int _cursorIndexOfBaselineCost = CursorUtil.getColumnIndexOrThrow(_cursor, "baselineCost");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfGoalType = CursorUtil.getColumnIndexOrThrow(_cursor, "goalType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfActive = CursorUtil.getColumnIndexOrThrow(_cursor, "active");
          final AddictionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpCustomUnit;
            if (_cursor.isNull(_cursorIndexOfCustomUnit)) {
              _tmpCustomUnit = null;
            } else {
              _tmpCustomUnit = _cursor.getString(_cursorIndexOfCustomUnit);
            }
            final double _tmpBaselineAmount;
            _tmpBaselineAmount = _cursor.getDouble(_cursorIndexOfBaselineAmount);
            final String _tmpBaselineFrequency;
            _tmpBaselineFrequency = _cursor.getString(_cursorIndexOfBaselineFrequency);
            final double _tmpBaselineCost;
            _tmpBaselineCost = _cursor.getDouble(_cursorIndexOfBaselineCost);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final String _tmpGoalType;
            _tmpGoalType = _cursor.getString(_cursorIndexOfGoalType);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final boolean _tmpActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfActive);
            _tmpActive = _tmp != 0;
            _result = new AddictionEntity(_tmpId,_tmpName,_tmpCategory,_tmpUnit,_tmpCustomUnit,_tmpBaselineAmount,_tmpBaselineFrequency,_tmpBaselineCost,_tmpCurrency,_tmpGoalType,_tmpStartDate,_tmpActive);
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
