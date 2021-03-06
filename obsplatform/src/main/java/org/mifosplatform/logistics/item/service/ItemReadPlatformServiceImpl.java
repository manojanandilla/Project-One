package org.mifosplatform.logistics.item.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mifosplatform.billing.chargecode.data.ChargesData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.item.data.UniteTypeData;
import org.mifosplatform.logistics.item.domain.ItemEnumType;
import org.mifosplatform.logistics.item.domain.ItemTypeData;
import org.mifosplatform.logistics.item.domain.UnitEnumType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ItemReadPlatformServiceImpl implements ItemReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<ItemData> paginationHelper = new PaginationHelper<ItemData>();

	@Autowired
	public ItemReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	


	@Override
	public List<EnumOptionData> retrieveItemClassType() {
		 final EnumOptionData hardware = ItemTypeData.ItemClassType(ItemEnumType.HARDWARE);
		 final EnumOptionData prepaidCard = ItemTypeData.ItemClassType(ItemEnumType.PREPAID_CARD);
	     final EnumOptionData softCharge = ItemTypeData.ItemClassType(ItemEnumType.SOFT_CHARGE);
	     final EnumOptionData event = ItemTypeData.ItemClassType(ItemEnumType.EVENT);
	     final List<EnumOptionData> categotyType = Arrays.asList(hardware, prepaidCard,softCharge,event);
	    return categotyType;
	 }

	@Override
	public List<EnumOptionData> retrieveUnitTypes() {
		final EnumOptionData meters = UniteTypeData.UnitClassType(UnitEnumType.METERS);
		final EnumOptionData pieces = UniteTypeData.UnitClassType(UnitEnumType.PIECES);
		final EnumOptionData hours = UniteTypeData.UnitClassType(UnitEnumType.HOURS);
		final EnumOptionData days = UniteTypeData.UnitClassType(UnitEnumType.DAYS);
		final List<EnumOptionData> categotyType = Arrays.asList(meters, pieces, hours, days);
		return categotyType;
	}



	@Override
	public List<ChargesData> retrieveChargeCode() {
		final String sql = "select s.id as id,s.charge_code as charge_code,s.charge_description as charge_description from b_charge_codes s  " +
		 		"where s.charge_type='NRC'";
		 
		 final RowMapper<ChargesData> rm = new ChargeMapper();
        return this.jdbcTemplate.query(sql, rm, new Object[] {});
     }


 private static final class ChargeMapper implements RowMapper<ChargesData> {

        @Override
        public ChargesData mapRow(final ResultSet rs,final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String chargeCode = rs.getString("charge_code");
            final String chargeDesc= rs.getString("charge_description");
            return new ChargesData(id,chargeCode,chargeDesc);
        }
}


@Override
public List<ItemData> retrieveAllItems() {
	
	context.authenticatedUser();
	SalesDataMapper mapper = new SalesDataMapper();
	String sql = "select " + mapper.schema()+" where  a.is_deleted='n'";
	return this.jdbcTemplate.query(sql, mapper, new Object[] {  });
}

private static final class SalesDataMapper implements
		RowMapper<ItemData> {

	public String schema() {
		return " a.id as id,a.item_code as itemCode,a.item_description as itemDescription,a.item_class as itemClass,a.units as units,a.charge_code as chargeCode,round(a.unit_price,2) price,a.warranty as warranty,"+
				"b.Used as used,b.Available as available,b.Total_items as totalItems from b_item_master a "+
				"left join ( Select item_master_id,Sum(Case When Client_id IS NULL "+
                "        Then 1 "+
                "        Else 0 "+
                " End) Available,"+
                "Sum(Case When Client_id Is Not NULL "+
                "         Then 1 "+
                "        Else 0 "+
                " End) Used,"+
                "Count(1) Total_items "+
                "From b_item_detail group by item_master_id ) b on a.id=b.item_master_id ";

	}

	@Override
	public ItemData mapRow(ResultSet rs, int rowNum)
			throws SQLException {

		final Long id = rs.getLong("id");
		final String itemCode = rs.getString("itemCode");
		final String itemDescription = rs.getString("itemDescription");
		final String itemClass = rs.getString("itemClass");
		final String units = rs.getString("units");
		final String chargeCode = rs.getString("chargeCode");
		final BigDecimal unitPrice = rs.getBigDecimal("price");
		final int warranty = rs.getInt("warranty");
		final Long used = rs.getLong("used");
		final Long available = rs.getLong("available");
		final Long totalItems = rs.getLong("totalItems");
		return new ItemData(id,itemCode,itemDescription,itemClass,units,chargeCode,warranty,unitPrice,used,available,totalItems);


	}
	}


@Override
public ItemData retrieveSingleItemDetails(final Long itemId) {

	context.authenticatedUser();
	SalesDataMapper mapper = new SalesDataMapper();
	String sql = "select " + mapper.schema()+" where a.id=? and  a.is_deleted='n'";
	return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { itemId });
}

@Override
public Page<ItemData> retrieveAllItems(SearchSqlQuery searchItems) {
	
	context.authenticatedUser();
	SalesDataMapper mapper = new SalesDataMapper();
	final StringBuilder sqlBuilder = new StringBuilder(200);
    sqlBuilder.append("select ");
    sqlBuilder.append(mapper.schema());
    sqlBuilder.append(" where a.is_deleted='n' ");
    
    String sqlSearch = searchItems.getSqlSearch();
    String extraCriteria = "";
    if (sqlSearch != null) {
    	sqlSearch=sqlSearch.trim();
    	extraCriteria = " and (a.item_description like '%"+sqlSearch+"%' OR" 
    			+ " a.item_code like '%"+sqlSearch+"%' )";
    			
    			
    }
        sqlBuilder.append(extraCriteria);
    
   /* if (StringUtils.isNotBlank(extraCriteria)) {
        sqlBuilder.append(extraCriteria);
    }*/


    if (searchItems.isLimited()) {
        sqlBuilder.append(" limit ").append(searchItems.getLimit());
    }

    if (searchItems.isOffset()) {
        sqlBuilder.append(" offset ").append(searchItems.getOffset());
    }

	return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
            new Object[] {}, mapper);
}



@Override
public List<ItemData> retrieveAuditDetails(final Long itemId) {
	
	String sql="select bia.id as id,bia.itemmaster_id as itemMasterId,bia.item_code as itemCode,bia.unit_price as unitPrice,"+
				"bia.changed_date as changedDate from b_item_audit bia where itemmaster_id=?";
	
	final RowMapper<ItemData> rm = new AuditMapper();

    return this.jdbcTemplate.query(sql, rm, new Object[] {itemId});
	
}
private static final class AuditMapper implements RowMapper<ItemData> {

    @Override
    public ItemData mapRow(final ResultSet rs,final int rowNum) throws SQLException {
    	
    	final Long id = rs.getLong("id");
    	final Long itemMasterId = rs.getLong("itemMasterId");
    	final String itemCode = rs.getString("itemCode");
    	final BigDecimal unitPrice = rs.getBigDecimal("unitPrice");
    	final Date changedDate = rs.getDate("changedDate");
        return new ItemData(id,itemMasterId,itemCode,unitPrice,changedDate);
    }
}

}


