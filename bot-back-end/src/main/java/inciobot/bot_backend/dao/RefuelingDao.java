package inciobot.bot_backend.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import inciobot.bot_backend.model.Refueling;
import inciobot.bot_ci.MonthCount;

@Repository
@Transactional
public class RefuelingDao extends AbstractDao {

	public void addRefueling(Refueling refueling) {
		getSession().save(refueling);
	}

	@SuppressWarnings("unchecked")
	public List<MonthCount> getPricePerMonth() {
		SQLQuery query = getSession().createSQLQuery(
				"select EXTRACT(MONTH FROM date) as month, EXTRACT(YEAR FROM date) as year, SUM(price) as count, fuel_type as type from refueling group by month, year, type ORDER BY month, year");

		query.setResultTransformer(Transformers.aliasToBean(MonthCount.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<MonthCount> getDistancePerMonth() {
		SQLQuery query = getSession().createSQLQuery(
				"select EXTRACT(MONTH FROM date) as month, EXTRACT(YEAR FROM date) as year, SUM(distance) as count from refueling group by month, year ORDER BY month, year");

		query.setResultTransformer(Transformers.aliasToBean(MonthCount.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<MonthCount> getAvgDistQuantityPerMonth() {
		SQLQuery query = getSession().createSQLQuery(
				"select a.month, a.year, cast(a.distance as float)/cast(a.kg as float) as count from (select extract(month from date) as month, extract(year from date) as year,sum(price) as sum,sum(distance) as distance,sum(fuelquantity) as kg from refueling where fuel_type = 'METANO'"
						+ "group by 1,2) as a");

		query.setResultTransformer(Transformers.aliasToBean(MonthCount.class));
		return query.list();
	}
}
