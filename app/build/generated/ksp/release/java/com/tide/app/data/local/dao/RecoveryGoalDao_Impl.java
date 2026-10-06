package com.tide.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tide.app.data.local.entity.RecoveryGoalEntity;
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
public final class RecoveryGoalDao_Impl implements RecoveryGoalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RecoveryGoalEntity> __insertionAdapterOfRecoveryGoalEntity;

  private final EntityDeletionOrUpdateAdapter<RecoveryGoalEntity> __deletionAdapterOfRecoveryGoalEntity;

  private final EntityDeletionOrUpdateAdapter<RecoveryGoalEntity> __updateAdapterOfRecoveryGoalEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public RecoveryGoalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecoveryGoalEntity = new EntityInsertionAdapter<RecoveryGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `recovery_goals` (`id`,`name`,`targetAmount`,`currentAmount`,`achieved`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecoveryGoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getTargetAmount());
        statement.bindDouble(4, entity.getCurrentAmount());
        final int _tmp = entity.getAchieved() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfRecoveryGoalEntity = new EntityDeletionOrUpdateAdapter<RecoveryGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `recovery_goals` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecoveryGoalEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRecoveryGoalEntity = new EntityDeletionOrUpdateAdapter<RecoveryGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `recovery_goals` SET `id` = ?,`name` = ?,`targetAmount` = ?,`currentAmount` = ?,`achieved` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecoveryGoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getTargetAmount());
        statement.bindDouble(4, entity.getCurrentAmount());
        final int _tmp = entity.getAchieved() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recovery_goals";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RecoveryGoalEntity recoveryGoal,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRecoveryGoalEntity.insertAndReturnId(recoveryGoal);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RecoveryGoalEntity recoveryGoal,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRecoveryGoalEntity.handle(recoveryGoal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RecoveryGoalEntity recoveryGoal,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRecoveryGoalEntity.handle(recoveryGoal);
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
  public Flow<List<RecoveryGoalEntity>> getAll() {
    final String _sql = "SELECT * FROM recovery_goals ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"recovery_goals"}, new Callable<List<RecoveryGoalEntity>>() {
      @Override
      @NonNull
      public List<RecoveryGoalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetAmount");
          final int _cursorIndexOfCurrentAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentAmount");
          final int _cursorIndexOfAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "achieved");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<RecoveryGoalEntity> _result = new ArrayList<RecoveryGoalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecoveryGoalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getDouble(_cursorIndexOfTargetAmount);
            final double _tmpCurrentAmount;
            _tmpCurrentAmount = _cursor.getDouble(_cursorIndexOfCurrentAmount);
            final boolean _tmpAchieved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAchieved);
            _tmpAchieved = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new RecoveryGoalEntity(_tmpId,_tmpName,_tmpTargetAmount,_tmpCurrentAmount,_tmpAchieved,_tmpCreatedAt);
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
  public Object getAllSync(final Continuation<? super List<RecoveryGoalEntity>> $completion) {
    final String _sql = "SELECT * FROM recovery_goals ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RecoveryGoalEntity>>() {
      @Override
      @NonNull
      public List<RecoveryGoalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTargetAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "targetAmount");
          final int _cursorIndexOfCurrentAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentAmount");
          final int _cursorIndexOfAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "achieved");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<RecoveryGoalEntity> _result = new ArrayList<RecoveryGoalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecoveryGoalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpTargetAmount;
            _tmpTargetAmount = _cursor.getDouble(_cursorIndexOfTargetAmount);
            final double _tmpCurrentAmount;
            _tmpCurrentAmount = _cursor.getDouble(_cursorIndexOfCurrentAmount);
            final boolean _tmpAchieved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAchieved);
            _tmpAchieved = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new RecoveryGoalEntity(_tmpId,_tmpName,_tmpTargetAmount,_tmpCurrentAmount,_tmpAchieved,_tmpCreatedAt);
            _result.add(_item);
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
