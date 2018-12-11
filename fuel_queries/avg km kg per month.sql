select a.mon, a.yyyy, cast(a.distance as float)/cast(a.kg as float) as kmkg
from (select to_char(date,'Mon') as mon,
       extract(year from date) as yyyy,
       sum(price) as sum,
       sum(distance) as distance,
       sum(fuelquantity) as kg
from refueling
group by 1,2) as a