package pl.bsp.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "parentalControlPolicy")
public class ParentalControlPolicy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_policy", nullable = false)
	private long id;
	@Column(name = "description", nullable = true)
	String description;
	@Column(name = "resource_name", nullable = false)
	String resourceName;
	@Column(name = "action", nullable = false)
	String action;
	@Column(name = "start_time", nullable = false)
	String startTime;
	@Column(name = "end_time", nullable = false)
	String endTime;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_user", nullable = false)
	private User user;

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "resource")
//	private Set<Action> actions = new HashSet<>();


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
