package org.mifosplatform.organisation.smartsearch.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.smartsearch.data.AdvanceSearchData;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AdvanceSearchReadPlafformServiceImpl implements AdvanceSearchReadPlafformService{
	
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<AdvanceSearchData> paginationHelper = new PaginationHelper<AdvanceSearchData>();
	
@Autowired
public AdvanceSearchReadPlafformServiceImpl(final PlatformSecurityContext securityContext,
		                   final TenantAwareRoutingDataSource  dataSource){
	
	this.context=securityContext;
	this.jdbcTemplate=new JdbcTemplate(dataSource);
	
}
	

  @Transactional
  @Override
  public Page<AdvanceSearchData> retrieveAllSearchData(SearchParameters searchParameters) {
	  try{
		  this.context.authenticatedUser();
		  final AdvanceSearchMapper advanceSearchMapper=new AdvanceSearchMapper();
		  StringBuilder stringBuilder=new StringBuilder(advanceSearchMapper.schema());
		  
		  if(searchParameters.getSqlSearch() != null){
			  stringBuilder.append(" AND (t.description LIKE '"+searchParameters.getSqlSearch()+"' OR c.display_name LIKE '"+searchParameters.getSqlSearch()+"')");
		  }
		  
		  if(searchParameters.getCategory() != null){
			  stringBuilder.append(" AND v.id="+searchParameters.getCategory());
		  }
		  if(searchParameters.getStatus() != null){
			  stringBuilder.append(" AND t.status="+searchParameters.getStatus());
		  }
		  if(searchParameters.getAssignedTo() != null){
			  stringBuilder.append(" AND t.assigned_to ="+searchParameters.getAssignedTo());
		  }
		  if(searchParameters.getClosedBy() != null){
			  stringBuilder.append(" AND t.lastmodifiedby_id="+searchParameters.getClosedBy());
		  }
		  
		   stringBuilder.append(" order by t.id limit "+searchParameters.getLimit()+" offset "+searchParameters.getOffset());
		   return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",stringBuilder.toString(),
	                new Object[] {}, advanceSearchMapper);
		  
	  }catch(EmptyResultDataAccessException accessException){
		  return null;  
	  }
		
	}

  private static final class AdvanceSearchMapper implements RowMapper<AdvanceSearchData> {

      public String schema() {
          return " SELECT t.id AS id, c.display_name AS clientName,c.id as clientId,c.account_no AS accountNo,t.ticket_date as transactionDate," +
          		"  t.status as status,v.code_value as category,a.username as userName FROM b_ticket_master t, m_client c, m_code_value  v," +
          		" m_appuser a WHERE  c.id = t.client_id and t.problem_code=v.id AND a.id=t.createdby_id ";
      }

      @Override
      public AdvanceSearchData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

          final Long id = rs.getLong("id");
          final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
          final Long clientId = rs.getLong("clientId");
          final String accountNo = rs.getString("accountNo");
          final String clientName = rs.getString("clientName");
          final String status = rs.getString("status");
          final String category = rs.getString("category");
          final String userName = rs.getString("userName");

          return new AdvanceSearchData(id,clientId,accountNo,clientName,transactionDate,category,status,userName);
      }
  }
}