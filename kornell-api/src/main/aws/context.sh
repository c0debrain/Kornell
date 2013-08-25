CONTEXT=$( cat <<EOF
<Context path="">
	<!-- TODO: Make this file development only, generate from environment variables in production  -->
	<!-- TODO: Consider Read Replicas http://dev.mysql.com/doc/refman/5.1/en/connector-j-reference-replication-connection.html -->	    	
	<Resource name="jdbc/KornellDS" 
		auth="Container" 
	    type="javax.sql.DataSource"
		maxActive="100"  
		maxIdle="10" 
		maxWait="10000"
		driverClassName="com.mysql.jdbc.Driver"
		username="kornell"
		password="42kornell73"
		url="${JDBC_CONNECTION_STRING}"
		validationQuery="select 42"
		validationQueryTimeout="2"
		testOnBorrow="true" 
		testOnReturn="false"/>
	
	<Realm className="org.apache.catalina.realm.DataSourceRealm"
		dataSourceName="jdbc/KornellDS"
		localDataSource="true"
   		userTable="Password" 
   		userNameCol="username"
   		userCredCol="password"
   		userRoleTable="Role" 
   		roleNameCol="role"
   		digest="SHA-256"/>
	
</Context>
EOF
)
echo $CONTEXT