import React, { useMemo, useState } from "react";
import { useRouteMatch, useHistory } from "react-router-dom";
import { Grid, MenuItem, Button } from "@material-ui/core";
import sharedStyles from "../styles";
import { useFetch, useWorkflowNamesAndVersions } from "../../utils/query";
import { makeStyles } from "@material-ui/styles";
import WorkflowDAG from "../../components/diagram/WorkflowDAG";
import WorkflowGraph from "../../components/diagram/WorkflowGraph";
import { Helmet } from "react-helmet";
import { ReactJson, LinearProgress, Heading, Select } from "../../components";
import _ from "lodash";

const useStyles = makeStyles(sharedStyles);

export default function WorkflowDefinition() {
  const classes = useStyles();
  const history = useHistory();
  const match = useRouteMatch();
  const workflowName = _.get(match, "params.name");
  const version = _.get(match, "params.version");
  const [showDag, setShowDag] = useState(true);

  let path = `/metadata/workflow/${workflowName}`;
  if (version) path += `?version=${version}`;

  const { data: workflow, isFetching } = useFetch(path);
  const dag = useMemo(
    () => workflow && new WorkflowDAG(null, workflow),
    [workflow]
  );

  const namesAndVersions = useWorkflowNamesAndVersions();
  let versions = namesAndVersions.get(workflowName) || [];

  const {data:moniData, isFetching:moniFetching} = useFetch('/moni?query=sum(ogv_custom_activity_workflow_worker)%20by%20(workerName)');
  return (
    <div className={classes.wrapper}>
      <Helmet>
        <title>Conductor UI - Workflow Definition - {match.params.name}</title>
      </Helmet>
      <div className={classes.header} style={{ paddingBottom: 20 }}>
        <Heading level={1}>Workflow Definition</Heading>
        <Heading level={4} gutterBottom>
          {match.params.name}
        </Heading>
        <Grid container>
          <Grid item xs={2}>
            <Select
              value={_.isUndefined(version) ? "" : version}
              displayEmpty
              renderValue={(v) => (v === "" ? "Latest Version" : v)}
              onChange={(evt) =>
                history.push(
                  `/workflowDef/${workflowName}${
                    evt.target.value === "" ? "" : "/"
                  }${evt.target.value}`
                )
              }
            >
              <MenuItem value="">Latest Version</MenuItem>
              {versions.map((ver) => (
                <MenuItem value={ver} key={ver}>
                  Version {ver}
                </MenuItem>
              ))}
            </Select>
          </Grid>
          <Grid item xs={2}>
            <Button color='default' variant='outlined' children={showDag?'切换到json显示':'切换到dag显示'} onClick={()=>setShowDag(!showDag)}/>
          </Grid>
        </Grid>
      </div>
      {isFetching && <LinearProgress />}
      <div className={classes.tabContent}>
        <Grid container>
          {
            showDag?
            <Grid item xs={12}>
              {dag && <WorkflowGraph dag={dag} moni={moniData} />}
            </Grid>:
            <Grid item xs={12}>
              {workflow && <ReactJson src={workflow} />}
            </Grid>
          }
        </Grid>
      </div>
    </div>
  );
}
