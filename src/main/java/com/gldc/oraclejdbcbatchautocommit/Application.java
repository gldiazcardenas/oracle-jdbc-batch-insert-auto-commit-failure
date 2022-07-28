package com.gldc.oraclejdbcbatchautocommit;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) {
		String sql = "INSERT INTO BATCH_INSERT (ID,TEXT) VALUES (?,?)";

		List<Row> rows = getRows();

		for (List<Row> batchList : Lists.partition(rows, 5)) {
			int[] results = null;
			try {
				results = jdbcTemplate.batchUpdate(sql, getBatchPrepStmntCallInsert(batchList));
				if (Ints.contains(results, Statement.EXECUTE_FAILED)) {
					System.out.println("Some failed: " + Arrays.toString(results));
				}
				else {
					System.out.println("Inserted correctly: " + IntStream.of(results).sum());
				}
			}
			catch (Exception e) {
				System.out.println("Failed: " + e.getLocalizedMessage() + ", results: " + Arrays.toString(results));
			}
		}

		List<Row> insertedRows = jdbcTemplate.query("SELECT * FROM BATCH_INSERT", BeanPropertyRowMapper.newInstance(Row.class));

		System.out.println("Inserted Rows: " + insertedRows.size());
	}

	protected BatchPreparedStatementSetter getBatchPrepStmntCallInsert(List<Row> rows) {
		return new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Row row = rows.get(i);
				ps.setLong(1, row.getId());
				ps.setString(2, row.getText());
			}

			@Override
			public int getBatchSize() {
				return rows.size();
			}
		};
	}

	private List<Row> getRows() {
		List<Row> rows = new ArrayList<>();
		rows.add(new Row(1L, "shorttext"));
		rows.add(new Row(2L, "shorttext"));
		rows.add(new Row(3L, "shorttext"));
		rows.add(new Row(4L, "shorttext"));
		rows.add(new Row(5L, "shorttext"));
		rows.add(new Row(6L, "shorttext"));
		rows.add(new Row(7L, "shorttext"));
		rows.add(new Row(8L, "longlonglonglonglonglonglonglonglonglonglonglongtext"));
		rows.add(new Row(9L, "shorttext"));
		rows.add(new Row(10L, "shorttext"));
		rows.add(new Row(11L, "shorttext"));
		rows.add(new Row(12L, "shorttext"));
		rows.add(new Row(13L, "shorttext"));
		rows.add(new Row(14L, "shorttext"));
		rows.add(new Row(15L, "shorttext"));
		rows.add(new Row(16L, "shorttext"));
		rows.add(new Row(17L, "shorttext"));
		rows.add(new Row(18L, "shorttext"));
		rows.add(new Row(19L, "shorttext"));
		rows.add(new Row(20L, "shorttext"));
		return rows;
	}

}
